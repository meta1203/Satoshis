package dogecoin.mc.dogecoins.database.serialization;

import com.google.gson.*;
import dogecoin.mc.dogecoins.database.AccountEntry;

import java.lang.reflect.Type;
import java.util.UUID;

public class AccountSerializer implements JsonSerializer<AccountEntry>, JsonDeserializer<AccountEntry> {

    @Override
    public JsonElement serialize(AccountEntry src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement serializedUuid = context.serialize(src.getPlayerUuid().toString());
        JsonElement serializedBalance = context.serialize(src.getBalance());
        JsonElement serializedAddress = context.serialize(src.getDogeAddress());

        JsonObject object = new JsonObject();
        object.add("u", serializedUuid);
        object.add("b", serializedBalance);
        object.add("d", serializedAddress);
        return object;
    }

    @Override
    public AccountEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        UUID playerUuid = UUID.fromString(object.get("u").getAsString());
        double balance = object.get("b").getAsDouble();
        String dogeAddress = object.get("d").getAsString();

        return new AccountEntry(playerUuid, balance, dogeAddress);
    }

}
