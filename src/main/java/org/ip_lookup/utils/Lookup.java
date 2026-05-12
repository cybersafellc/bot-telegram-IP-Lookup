package org.ip_lookup.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.ip_lookup.model.Connections;
import org.ip_lookup.Main;

import java.io.IOException;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import java.io.File;
import java.net.InetAddress;

public class Lookup{
    public final static Connections databases;
    public final static ObjectMapper maper;

    static {
        databases = new Connections();
        maper = new ObjectMapper();
    }

    public static String lookupv2 (String ipAddress) throws IOException {
        File dbAsn = new File("geolite2-db/GeoLite2-ASN.mmdb");
        File dbCity = new File("geolite2-db/GeoLite2-City.mmdb");
        File dbCountry = new File("geolite2-db/GeoLite2-Country.mmdb");

        ArrayList<String> datas = new ArrayList<>();

        try(DatabaseReader reader = new DatabaseReader.Builder(dbAsn).build()){
            InetAddress ipAddr = InetAddress.getByName(ipAddress);
            AsnResponse response = reader.asn(ipAddr);
            datas.add(response.toJson());
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        try(DatabaseReader reader = new DatabaseReader.Builder(dbCity).build()){
            InetAddress ipAddr = InetAddress.getByName(ipAddress);
            CityResponse response = reader.city(ipAddr);
            datas.add(response.toJson());
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        try(DatabaseReader reader = new DatabaseReader.Builder(dbCountry).build()){
            InetAddress ipAddr = InetAddress.getByName(ipAddress);
            CountryResponse response = reader.country(ipAddr);
            datas.add(response.toJson());
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        return datas.toString();
    }

    public static boolean validation(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return false;
        }

        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        int[] nums = new int[4];

        for (int i = 0; i < 4; i++) {
            if (!parts[i].matches("\\d+")) {
                return false;
            }

            if (parts[i].length() > 1 && parts[i].startsWith("0")) {
                return false;
            }

            int n = Integer.parseInt(parts[i]);
            if (n < 0 || n > 255) {
                return false;
            }

            nums[i] = n;
        }

        if (nums[0] == 127) {
            return false;
        }

        if (nums[0] == 10) {
            return false;
        }

        if (nums[0] == 172 && nums[1] >= 16 && nums[1] <= 31) {
            return false;
        }

        if (nums[0] == 192 && nums[1] == 168) {
            return false;
        }

        if (nums[0] == 169 && nums[1] == 254) {
            return false;
        }

        return true;
    }

    public static String escapeMarkdownV2(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }

    public static String prettyJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Object obj = mapper.readValue(json, Object.class);

            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);

        } catch (Exception e) {
            return "Invalid JSON";
        }
    }
}
