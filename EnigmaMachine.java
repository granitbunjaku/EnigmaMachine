import java.time.LocalDate;
import java.util.*;

public class EnigmaMachine implements IEnigmaMachine {
    private SecretKey secretKey;
    private static final Scanner SCANNER = new Scanner(System.in);
    private final IEnigmaMachineConfiguration enigmaMachineConfiguration;
    private static final String ENCRYPT_OPERATION = "encrypt";
    private static final String DECRYPT_OPERATION = "decrypt";
    private int firstRotorRotations = 0;
    private int secondRotorRotations = 0;

    public EnigmaMachine() {
        enigmaMachineConfiguration = new EnigmaMachineConfiguration();
        secretKey = new SecretKey();
    }

    public String encrypt() {
        return runEnigma(ENCRYPT_OPERATION);
    }

    public String decrypt() {
        return runEnigma(DECRYPT_OPERATION);
    }

    private String runEnigma(String operation) {
        // StringBuilder to show the final result
        StringBuilder wordOperatedOn = new StringBuilder();

        // Calling methods to set up the machine settings
        if(enigmaMachineConfiguration.anySettingEmpty()) {
            enigmaMachineConfiguration.setFirstRotorSettings();
            enigmaMachineConfiguration.setSecondRotorSettings();
            enigmaMachineConfiguration.setThirdRotorSettings();
        } else {
            askForRotorSettings();
        }

        if(enigmaMachineConfiguration.getPlugboardSettings().isEmpty()) {
            enigmaMachineConfiguration.setPlugboardSettings();
        } else {
            askForPlugboardSettings();
        }

        enigmaMachineConfiguration.setReflectorSettings();


        if(operation.equalsIgnoreCase(ENCRYPT_OPERATION)) {
            if(secretKey.dateOfKey == null || secretKey.dateOfKey.getDay() != LocalDate.now().getDayOfMonth()) {
                System.out.println("Enter a secret key");
                String keyFromUser = SCANNER.nextLine();

                while (keyFromUser.length() < 3) {
                    System.out.println("Enter a secret key");
                    keyFromUser = SCANNER.nextLine();
                }

                secretKey.key.append(keyFromUser, 0, 3);
                secretKey.dateOfKey = new Date();
            }

            String originalKey = secretKey.key.toString();

            encryptKey(secretKey.key);
            moveRotorsByKey(originalKey);

            wordOperatedOn.append(secretKey.key);
        }

        System.out.println("Enter letter to " + operation);

        // Creating the array of characters that holds letters to encrypt, we will later on split each character of the word user types into this array
        ArrayList<Character> lettersToEncrypt = new ArrayList<>();
        String stringToEncrypt = SCANNER.nextLine();

        if(operation.equalsIgnoreCase(DECRYPT_OPERATION)) {
            secretKey.key.append(stringToEncrypt, 0, 3);
            decryptKey(secretKey.key);
            moveRotorsByKey(secretKey.key.toString());
            stringToEncrypt = stringToEncrypt.substring(3);
        }

        // The split of the word we talked about above
        for(int i = 0; i < stringToEncrypt.length(); i++) {
            lettersToEncrypt.add(Character.toUpperCase(stringToEncrypt.charAt(i)));
        }

        // Encryption/Decryption for each word
        for(int i = 0; i < lettersToEncrypt.size(); i++) {
            enigmaOperation(operation, lettersToEncrypt.get(i), wordOperatedOn);
        }

        // Printing the final result
        return wordOperatedOn.toString();
    }

    private void encryptKey(StringBuilder result) {
        String normalKey = result.toString();
        result.delete(0,3);

        for(int i = 0; i < normalKey.length(); i++) {
            enigmaOperation(ENCRYPT_OPERATION, normalKey.charAt(i), result);
        }
    }


    private void decryptKey(StringBuilder result) {
        String normalKey = result.toString();
        result.delete(0,3);

        for(int i = 0; i < normalKey.length(); i++) {
            enigmaOperation(DECRYPT_OPERATION, normalKey.charAt(i), result);
        }
    }

    public void moveRotorsByKey(String key) {
        while(enigmaMachineConfiguration.getFirstRotorSettings().get('A') != key.charAt(0)) {
            rotateSpecificRotor(enigmaMachineConfiguration.getFirstRotorSettings());
        }

        while(enigmaMachineConfiguration.getSecondRotorSettings().get('A') != key.charAt(1)) {
            rotateSpecificRotor(enigmaMachineConfiguration.getSecondRotorSettings());
        }

        while(enigmaMachineConfiguration.getThirdRotorSettings().get('A') != key.charAt(2)) {
            rotateSpecificRotor(enigmaMachineConfiguration.getThirdRotorSettings());
        }
    }

    /**
     * The method that does encryption/decryption
     * <p>
     * This method is void and returns nothing. It's a method that enigma
     * uses to rotate the rotor for one position after each letter. It's
     * important because it plays an important role on scrambling of letters
     * @param operation encrypt if encrypting, decrypt if decrypting
     * @param letterToEncrypt The letter to encrypt/decrypt
     * @param result The StringBuilder that result will be on
     */
    private void enigmaOperation(String operation, char letterToEncrypt, StringBuilder result) {
        if(Character.isWhitespace(letterToEncrypt)) { result.append(letterToEncrypt); return;}
        else if (letterToEncrypt < 'A' || letterToEncrypt > 'Z') return;

        // Swapping the letter with the one on plugboard if it's connected to any, if not the letter doesn't change
        Character firstPlugboardEncryption = enigmaMachineConfiguration.getPlugboardSettings().getOrDefault(letterToEncrypt, letterToEncrypt);

        Character rotorEncryption;

        // If it's decryption, search for the letter in values and get the key
        if(operation.equalsIgnoreCase(DECRYPT_OPERATION)) {
            rotorEncryption = decryptFromRotor(firstPlugboardEncryption, enigmaMachineConfiguration.getThirdRotorSettings());
            rotorEncryption = decryptFromRotor(rotorEncryption, enigmaMachineConfiguration.getSecondRotorSettings());
            rotorEncryption = decryptFromRotor(rotorEncryption, enigmaMachineConfiguration.getFirstRotorSettings());
            rotorEncryption = enigmaMachineConfiguration.getReflectorSettings().get(rotorEncryption);
            rotorEncryption = decryptFromRotor(rotorEncryption, enigmaMachineConfiguration.getThirdRotorSettings());
            rotorEncryption = decryptFromRotor(rotorEncryption, enigmaMachineConfiguration.getSecondRotorSettings());
            rotorEncryption = decryptFromRotor(rotorEncryption, enigmaMachineConfiguration.getFirstRotorSettings());
        } else {
            // If it's encryption, get the value of the key with value of the letter we are encrypting
            rotorEncryption = enigmaMachineConfiguration.getFirstRotorSettings().get(firstPlugboardEncryption);
            rotorEncryption = enigmaMachineConfiguration.getSecondRotorSettings().get(rotorEncryption);
            rotorEncryption = enigmaMachineConfiguration.getThirdRotorSettings().get(rotorEncryption);
            rotorEncryption = enigmaMachineConfiguration.getReflectorSettings().get(rotorEncryption);
            rotorEncryption = enigmaMachineConfiguration.getFirstRotorSettings().get(rotorEncryption);
            rotorEncryption = enigmaMachineConfiguration.getSecondRotorSettings().get(rotorEncryption);
            rotorEncryption = enigmaMachineConfiguration.getThirdRotorSettings().get(rotorEncryption);
        }

        // rotating of rotor for one position
        rotateRotor();

        // Swapping the letter with the one on plugboard if it's connected to any, if not the letter doesn't change
        Character secondPlugboardEncryption = enigmaMachineConfiguration.getPlugboardSettings().getOrDefault(rotorEncryption, rotorEncryption);

        // Adding the letter to the final result
        result.append(secondPlugboardEncryption);
    }

    private Character decryptFromRotor(Character plugboardEncryptedLetter, Map<Character, Character> rotorSettings) {
        Character rotorEncryption = null;

        for(Map.Entry<Character, Character> entry : rotorSettings.entrySet()) {
            if(entry.getValue() == plugboardEncryptedLetter) {
                rotorEncryption = entry.getKey();
                break;
            }
        }

        return rotorEncryption;
    }

    /**
     * Rotates the rotor for one position
     * <p>
     * This method is void and returns nothing. It's a method that enigma
     * uses to rotate the rotor for one position after each letter. It's
     * important because it plays an important role on scrambling of letters
     */
    private void rotateRotor() {
        rotateSpecificRotor(enigmaMachineConfiguration.getFirstRotorSettings());
        firstRotorRotations++;

        if(firstRotorRotations == 26) {
            firstRotorRotations = 0;
            rotateSpecificRotor(enigmaMachineConfiguration.getSecondRotorSettings());
            secondRotorRotations++;

            if(secondRotorRotations == 26) {
                secondRotorRotations = 0;
                rotateSpecificRotor(enigmaMachineConfiguration.getThirdRotorSettings());
            }
        }
    }

    private void rotateSpecificRotor(Map<Character, Character> rotorSettings) {
        Character theChar = rotorSettings.get('A');

        for (char ch = 'A'; ch <= 'Z'; ch++) {
            char newCh = ch+1 != 91 ? rotorSettings.get((char) (ch+1)) : theChar;
            rotorSettings.replace(ch, newCh);
        }
    }

    private void askForRotorSettings() {
        System.out.println("Do you want to change rotor settings? (yes/no)");
        String response = SCANNER.nextLine().toLowerCase();

        if (response.equals("yes")) {
            enigmaMachineConfiguration.setFirstRotorSettings();
            enigmaMachineConfiguration.setSecondRotorSettings();
            enigmaMachineConfiguration.setThirdRotorSettings();
        }
    }

    private void askForPlugboardSettings() {
        System.out.println("Do you want to change plugboard settings? (yes/no)");
        String response = SCANNER.nextLine().toLowerCase();

        if (response.equals("yes")) {
            enigmaMachineConfiguration.setPlugboardSettings();
        }
    }

}
