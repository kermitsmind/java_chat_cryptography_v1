package chat;//Ogolnie wzialem to znowu z internetów, ale widzę, że samo szyfrowanie i deszyfrowanie jest dość zabawnie zrobione, bo nie używa kodu ASCII tylko bazuje na numerze w alfabecie. Tak czy inaczej jest to bardzo prosty program.

import java.util.*;

public class OneTimePad{
    public static String getKey(String text) {
        return RandomKey(text.length()); //generowanie klucza
    }
    public static String getEncryptedText(String text, String key) {
        return  OTPEncryption(text,key);
    }
    public static String getDecryptedText(String text, String key) {
        return  OTPDecryption(text,key);
    }

    public static String RandomKey(int len){ //trywialne generowanie prostego klucza
        Random r = new Random();
        String key = "";
        for(int x=0;x<len;x++)
            key = key + (char) (r.nextInt(26) + 'A');
        return key;
    }

    public static String OTPEncryption(String text,String key){ //szyfrowanie OTP
        String alphaU = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphaL = "abcdefghijklmnopqrstuvwxyz";

        int len = text.length(); //dlugosc tekstu

        String sb = ""; //definiowanie zmiennej, w ktorej zapisujemy zaszyfrowane
        for(int x=0;x<len;x++){ //petla po calym tekscie
            char get = text.charAt(x); //bierzemy po jednej literce z tekstu
            char keyget = key.charAt(x); //bierzemy po jednej literce z klucza
            if(Character.isUpperCase(get)){ //sprawdza to nam czy duza literka w tekscie
                int index = alphaU.indexOf(get); //bierze numer duzej litery z alfabetu
                int keydex = alphaU.indexOf(Character.toUpperCase(keyget));//bierze numer z alfabetu z litery klucza

                int total = (index + keydex) % 26; //dodaje je i dzieli modulo 26

                sb = sb+ alphaU.charAt(total); //a tutaj dodaje nam do tego zakodowanego
            }
            else if(Character.isLowerCase(get)){// sprawdza czy mala, reszta analogicznie
                int index = alphaL.indexOf(get);
                int keydex = alphaL.indexOf(Character.toLowerCase(keyget));

                int total = (index + keydex) % 26;

                sb = sb+ alphaL.charAt(total);
            }
            else{ //jak nie to dopisujemy
                sb = sb + get;
            }
        }

        return sb;//zwracamy nasze zaszyfrowane
    }
    public static String OTPDecryption(String text,String key){
        String alphaU = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphaL = "abcdefghijklmnopqrstuvwxyz";
        //tutaj analogicznie do szyfrowania, bo samo szyfrowanie jest trywialne
        int len = text.length();

        String sb = "";
        for(int x=0;x<len;x++){
            char get = text.charAt(x);
            char keyget = key.charAt(x);
            if(Character.isUpperCase(get)){
                int index = alphaU.indexOf(get);
                int keydex = alphaU.indexOf(Character.toUpperCase(keyget));

                int total = (index - keydex) % 26;
                total = (total<0)? total + 26 : total;

                sb = sb+ alphaU.charAt(total);
            }
            else if(Character.isLowerCase(get)){
                int index = alphaL.indexOf(get);
                int keydex = alphaL.indexOf(Character.toLowerCase(keyget));

                int total = (index - keydex) % 26;
                total = (total<0)? total + 26 : total;

                sb = sb+ alphaL.charAt(total);
            }
            else{
                sb = sb + get;
            }
        }

        return sb;
    }

}