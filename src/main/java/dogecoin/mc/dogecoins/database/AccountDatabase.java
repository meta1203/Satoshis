package dogecoin.mc.dogecoins.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.database.serialization.AccountSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AccountDatabase {

    private static HashMap<UUID, AccountEntry> accountHashMap;
    private static File accountsFile;
    private static Gson gson;

    public static void initialize(Dogecoins plugin) {
        accountHashMap = new HashMap<>();
        gson = new GsonBuilder()
                .registerTypeAdapter(AccountEntry.class, new AccountSerializer())
                .create();
        accountsFile = new File(plugin.getDataFolder(), "accounts.json");

        loadData();
    }

    public static void cleanup(Dogecoins plugin) {
        saveData();
    }

    private static void loadData() {
        Type accountsListType = new TypeToken<ArrayList<AccountEntry>>() {}.getType();
        ArrayList<AccountEntry> accountsList;
        try {
            accountsFile.createNewFile();
            accountsList = gson.fromJson(new InputStreamReader(new FileInputStream(accountsFile)), accountsListType);
            accountsList.forEach(accountEntry -> accountHashMap.put(accountEntry.getPlayerUuid(), accountEntry));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveData() {
        ArrayList<AccountEntry> accountsList = new ArrayList<>();
        accountHashMap.values().forEach(accountsList::add);
        try {
            FileWriter writer = new FileWriter(accountsFile);
            writer.write(gson.toJson(accountsList));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(AccountEntry accountEntry) {
        accountHashMap.put(accountEntry.getPlayerUuid(), accountEntry);
        saveData();
    }

    public static AccountEntry getAccount(UUID playerUuid) {
        return accountHashMap.get(playerUuid);
    }

    public static ArrayList<AccountEntry> getAccounts() {
        return new ArrayList<>(accountHashMap.values());
    }

    public static ArrayList<String> getAddresses() {
        ArrayList<String> addresses = new ArrayList<>();
        accountHashMap.values().forEach(accountEntry -> addresses.add(accountEntry.getDogeAddress()));
        return addresses;
    }

}
