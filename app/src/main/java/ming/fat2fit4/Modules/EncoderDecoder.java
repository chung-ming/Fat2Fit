package ming.fat2fit4.Modules;

/**
 * Created by Ernie&Ming on 29-Jan-17.
 */

public class EncoderDecoder {
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
}
