package org.ip_lookup.callbaks;

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
import org.ip_lookup.Main;
import org.ip_lookup.Secret;

import java.io.IOException;
import java.lang.Exception;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import java.io.File;
import java.net.InetAddress;

public class Listen  implements UpdatesListener{
    @Override
    public int process(List<Update> updates) {
        for(Update update : updates){
            if(update.message() != null){
                long chatId = update.message().chat().id();
                String message = update.message().text();
                SendResponse response;
                if(validation(message)){
                    String result = null;
                    try {
//                        result = formatJson(lookupv2(message));
                          result = formatJson(lookupv2(message));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = Main.bot.execute(
                            new SendMessage(chatId, result)
                    );
                }else{
                    response = Main.bot.execute(new SendMessage(chatId, "The format incorect, please input valid ip address\nExamlple : 34.120.22.1"));
                }
                System.out.println(response.isOk());
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    public String lookupv2 (String ipAddress) throws IOException {
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
    public String lookup(String ipAdress) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(new Secret().getInternalApiLookup() + ipAdress)).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();

    }
    public boolean validation(String ipAddress) {
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

    public static String formatJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            JsonNode asnNode = root.get(0);
            JsonNode cityNode = root.get(1);
            JsonNode countryNode = root.get(2);

            JsonNode city = cityNode.get("city");
            JsonNode country = cityNode.get("country");
            JsonNode continent = cityNode.get("continent");
            JsonNode location = cityNode.get("location");
            JsonNode postal = cityNode.get("postal");
            JsonNode traits = cityNode.get("traits");
            JsonNode registered = cityNode.get("registered_country");

            // subdivision
            String regionName = "Unknown";
            String regionCode = "Unknown";
            if (cityNode.has("subdivisions") && cityNode.get("subdivisions").size() > 0) {
                JsonNode sub = cityNode.get("subdivisions").get(0);
                regionName = getSafe(sub.get("names"), "en");
                regionCode = getSafe(sub, "iso_code");
            }

            return "🌐 *IP Lookup Result*\n\n"

                    + "📌 *Basic Info*\n"
                    + "IP              : " + getSafe(asnNode, "ip_address") + "\n"
                    + "Network         : " + getSafe(asnNode, "network") + "\n"
                    + "ASN             : " + getSafe(asnNode, "autonomous_system_number") + "\n"
                    + "Organization    : " + getSafe(asnNode, "autonomous_system_organization") + "\n\n"

                    + "🌍 *Location*\n"
                    + "Continent       : " + getSafe(continent.get("names"), "en") + " (" + getSafe(continent, "code") + ")\n"
                    + "Country         : " + getSafe(country.get("names"), "en") + " (" + getSafe(country, "iso_code") + ")\n"
                    + "Registered Ctry : " + getSafe(registered.get("names"), "en") + "\n"
                    + "Region          : " + regionName + " (" + regionCode + ")\n"
                    + "City            : " + getSafe(city.get("names"), "en") + "\n"
                    + "Postal Code     : " + getSafe(postal, "code") + "\n\n"

                    + "📍 *Coordinates*\n"
                    + "Latitude        : " + getSafe(location, "latitude") + "\n"
                    + "Longitude       : " + getSafe(location, "longitude") + "\n"
                    + "Accuracy Radius : " + getSafe(location, "accuracy_radius") + "\n"
                    + "Metro Code      : " + getSafe(location, "metro_code") + "\n"
                    + "Timezone        : " + getSafe(location, "time_zone") + "\n\n"

                    + "🧠 *Traits (FULL)*\n"
                    + "IP              : " + getSafe(traits, "ip_address") + "\n"
                    + "Network         : " + getSafe(traits, "network") + "\n"
                    + "Anonymous       : " + getSafe(traits, "is_anonymous") + "\n"
                    + "Anonymous Proxy : " + getSafe(traits, "is_anonymous_proxy") + "\n"
                    + "Anonymous VPN   : " + getSafe(traits, "is_anonymous_vpn") + "\n"
                    + "Anycast         : " + getSafe(traits, "is_anycast") + "\n"
                    + "Hosting         : " + getSafe(traits, "is_hosting_provider") + "\n"
                    + "Legit Proxy     : " + getSafe(traits, "is_legitimate_proxy") + "\n"
                    + "Public Proxy    : " + getSafe(traits, "is_public_proxy") + "\n"
                    + "Residential     : " + getSafe(traits, "is_residential_proxy") + "\n"
                    + "Satellite       : " + getSafe(traits, "is_satellite_provider") + "\n"
                    + "Tor Exit        : " + getSafe(traits, "is_tor_exit_node") + "\n\n"

                    + "🔎 *Geo Metadata*\n"
                    + "City GeoID      : " + getSafe(city, "geoname_id") + "\n"
                    + "Country GeoID   : " + getSafe(country, "geoname_id") + "\n"
                    + "Continent GeoID : " + getSafe(continent, "geoname_id") + "\n";

        } catch (Exception e) {
            return "❌ Gagal parsing JSON";
        }
    }

    public static String getSafe(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return "Unknown";
        }
        return node.get(field).asText();
    }
}
