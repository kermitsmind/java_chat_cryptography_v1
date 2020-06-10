package chat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OneTimePad {

//    public String textToEncrypt = "";
//    public String textToDecrypt = "";
//    public String encryptedText= "";
//    public String decryptedText= "";

    public static String doStaff(String choice, String textToChange) {

        String controlName = "plik";
        generatePad(controlName, 5, 60, 32, 126);
        String text= textToChange ;
//        String encrypted = parse(true, controlName, text.replaceAll(" ", ""));
//        String decrypted = parse(false, controlName, encrypted);

        generatePad(controlName, 5, 60, 32, 126);
       String  encrypted = parse(true, controlName, text); //tutaj jest zaszyfrowana
       String  decrypted = parse(false, controlName, text); //a tutaj odszyfrowana

//        System.out.println(); //a tutaj wypisywańsko
//        System.out.println("Input text     = " + text);
//        System.out.println("Encrypted text = " + encrypted);
//        System.out.println("Decrypted text = " + decrypted);
        if(choice == "encrypt"){
            return encrypted;
        }
            return decrypted ;
    }

    private static String parse(boolean encryptText, String controlName, String text) {
        StringBuilder sb = new StringBuilder();
        int minCh = 0;
        int maxCh = 0;
        Pattern minChPattern = Pattern.compile("^#  MIN_CH = ([\\d]+)$");
        Pattern maxChPattern = Pattern.compile("^#  MAX_CH = ([\\d]+)$");
        boolean validated = false;
        try (BufferedReader in = new BufferedReader(new FileReader(getFileName(controlName))); ) {
            String inLine = null;
            while ( (inLine = in.readLine()) != null ) {
                Matcher minMatcher = minChPattern.matcher(inLine);
                if ( minMatcher.matches() ) {
                    minCh = Integer.parseInt(minMatcher.group(1));
                    continue;
                }
                Matcher maxMatcher = maxChPattern.matcher(inLine);
                if ( maxMatcher.matches() ) {
                    maxCh = Integer.parseInt(maxMatcher.group(1));
                    continue;
                }
                if ( ! validated && minCh > 0 && maxCh > 0 ) {
                    validateText(text, minCh, maxCh);
                    validated = true;
                }
                //  # komentarz  - uzyty klucz.
                if ( inLine.startsWith("#") || inLine.startsWith("-") ) {
                    continue;
                }
                //  klucz do deskrypcji
                String key = inLine;
                if ( encryptText ) {
                    for ( int i = 0 ; i < text.length(); i++) {
                        sb.append((char) (((text.charAt(i) - minCh + key.charAt(i) - minCh) % (maxCh - minCh + 1)) + minCh));
                    }
                }
                else {
                    for ( int i = 0 ; i < text.length(); i++) {
                        int decrypt = text.charAt(i) - key.charAt(i);
                        if ( decrypt < 0 ) {
                            decrypt += maxCh - minCh + 1;
                        }
                        decrypt += minCh;
                        sb.append((char) decrypt);
                    }
                }
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private static void validateText(String text, int minCh, int maxCh) {
        //  Sprawdzanie legitnosci tekstu
        for ( char ch : text.toCharArray() ) {
            if ( ch != ' ' && (ch < minCh || ch > maxCh) ) {
                throw new IllegalArgumentException("Niestety podales cos zlego");
            }
        }

    }

    private static String getFileName(String controlName) {
        return controlName + ".txt";
    }

    private static void generatePad(String controlName, int keys, int keyLength, int minCh, int maxCh)
    //generowanie kluczaa
    {
        Random random = new Random();
        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(getFileName(controlName), false)); ) {
            writer.write("#  Lines starting with '#' are ignored.");
            writer.newLine();
            writer.write("#  Lines starting with '-' are previously used.");
            writer.newLine();
            writer.write("#  MIN_CH = " + minCh);
            writer.newLine();
            writer.write("#  MAX_CH = " + maxCh);
            writer.newLine();
            for ( int line = 0 ; line < keys ; line++ ) {
                StringBuilder sb = new StringBuilder();
                for ( int ch = 0 ; ch < keyLength ; ch++ ) {
                    sb.append((char) (random.nextInt(maxCh - minCh + 1) + minCh));
                }
                writer.write(sb.toString());
                writer.newLine();
            }
            writer.write("#  EOF");
            writer.newLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}