package org.ip_lookup.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import org.antibot.BotKiller;
import org.ip_lookup.model.Connections;

import java.io.IOException;
import java.lang.Exception;
import java.sql.SQLException;
import java.util.ArrayList;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import java.io.File;
import java.net.InetAddress;

public class Lookup{
    public final static Connections databases;
    public final static ObjectMapper maper;
    public final static BotKiller botKiller;

    static {
        databases = new Connections();
        maper = new ObjectMapper();
        try {
            botKiller = new BotKiller();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String lookupv2 (String ipAddress) throws IOException {
        ArrayList<String> datas = new ArrayList<>();
        datas.add(botKiller.asnLookupJson(ipAddress));
        datas.add(botKiller.cityLookupJson(ipAddress));
        datas.add(botKiller.countryLookupJson(ipAddress));
        datas.add(botKiller.romCheckerJson(ipAddress));
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
