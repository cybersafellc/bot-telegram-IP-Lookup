package org.ip_lookup.callbaks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
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
                    String result = formatJson(lookup(message));
                    response = Main.bot.execute(new SendMessage(chatId, result));
                }else{
                    response = Main.bot.execute(new SendMessage(chatId, "The format incorect, please input valid ip address\nExamlple : 34.120.22.1"));
                }
                System.out.println(response.isOk());
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
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
            JsonNode data = root.get("data");
            JsonNode cls = data.get("classification");

            return "🌐 IP Lookup Result\n\n"

                    + "📌 Basic Info\n"
                    + "IP          : " + getSafe(data, "query") + "\n"
                    + "Status      : " + getSafe(data, "status") + "\n"
                    + "Message     : " + getSafe(root, "message") + "\n\n"

                    + "🌍 Location\n"
                    + "Benua       : " + getSafe(data, "continent") + " (" + getSafe(data, "continentCode") + ")\n"
                    + "Negara      : " + getSafe(data, "country") + " (" + getSafe(data, "countryCode") + ")\n"
                    + "Region      : " + getSafe(data, "regionName") + " (" + getSafe(data, "region") + ")\n"
                    + "Kota        : " + getSafe(data, "city") + "\n"
                    + "Kode Pos    : " + getSafe(data, "zip") + "\n\n"

                    + "📍 Coordinate\n"
                    + "Latitude    : " + getSafe(data, "lat") + "\n"
                    + "Longitude   : " + getSafe(data, "lon") + "\n"
                    + "Timezone    : " + getSafe(data, "timezone") + "\n\n"

                    + "🏢 Network\n"
                    + "ISP         : " + getSafe(data, "isp") + "\n"
                    + "Org         : " + getSafe(data, "org") + "\n"
                    + "AS Number   : " + getSafe(data, "as_number") + "\n"
                    + "AS Name     : " + getSafe(data, "asname") + "\n"
                    + "AS Full     : " + getSafe(data, "as") + "\n\n"

                    + "🧠 Classification\n"
                    + "ASN         : " + getSafe(cls, "as_number") + "\n"
                    + "Nama AS     : " + getSafe(cls, "as_name") + "\n"
                    + "Negara AS   : " + getSafe(cls, "country_code") + "\n"
                    + "ROM         : " + getSafe(cls, "rom") + "\n\n"

                    + "🔎 Meta\n"
                    + "HTTP Code   : " + getSafe(root, "status") + "\n"
                    + "Error       : " + getSafe(root, "error") + "\n"
                    + "Reference   : " + getSafe(root, "reference");

        } catch (Exception e) {
            return "❌ Gagal parsing JSON";
        }
    }

    private static String getSafe(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return "-";
        }
        return node.get(field).asText();
    }
}
