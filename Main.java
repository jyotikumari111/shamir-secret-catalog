import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {

    static class Share {
        int x;
        BigInteger y;
        public Share(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    // Parses the JSON manually without external libraries
    public static List<Share> parseJson(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line.trim());
        br.close();

        String content = sb.toString().replaceAll("[{}\"]", "");
        String[] pairs = content.split(",");

        Map<String, String> map = new HashMap<>();
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2)
                map.put(kv[0].trim(), kv[1].trim());
        }

        int k = Integer.parseInt(map.get("k"));
        List<Share> shares = new ArrayList<>();

        for (String key : map.keySet()) {
            if (key.equals("k") || key.equals("n") || key.contains("."))
                continue;

            int x = Integer.parseInt(key);
            String baseKey = key + ".base";
            String valKey = key + ".value";

            String base = map.getOrDefault("base", map.get(baseKey));
            String value = map.getOrDefault("value", map.get(valKey));

            if (base == null || value == null)
                continue;

            int baseNum = Integer.parseInt(base);
            BigInteger y = new BigInteger(value, baseNum);
            shares.add(new Share(x, y));
        }

        // Sort shares by x
        shares.sort(Comparator.comparingInt(s -> s.x));
        return shares.subList(0, k);
    }

    public static BigInteger lagrangeInterpolation(List<Share> shares) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < shares.size(); i++) {
            BigInteger xi = BigInteger.valueOf(shares.get(i).x);
            BigInteger yi = shares.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < shares.size(); j++) {
                if (i == j) continue;

                BigInteger xj = BigInteger.valueOf(shares.get(j).x);
                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        List<Share> shares1 = parseJson("input1.json");
        List<Share> shares2 = parseJson("input2.json");

        BigInteger secret1 = lagrangeInterpolation(shares1);
        BigInteger secret2 = lagrangeInterpolation(shares2);

        System.out.println("Secret from input1.json: " + secret1);
        System.out.println("Secret from input2.json: " + secret2);
    }
}
