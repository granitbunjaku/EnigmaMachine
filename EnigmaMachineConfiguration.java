import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EnigmaMachineConfiguration implements IEnigmaMachineConfiguration {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final int MAX_PLUGBOARD_LENGTH = 52;

    private Map<Character, Character> firstRotorSettings = new HashMap<>();
    private Map<Character, Character> secondRotorSettings = new HashMap<>();
    private Map<Character, Character> thirdRotorSettings = new HashMap<>();
    private Map<Character, Character> plugboard = new HashMap<>();
    private Map<Character, Character> reflector = new HashMap<>();

    /**
     * Sets up the rotor settings
     * You can change the rotor settings and wiring configuration on the body method
     * <p>
     * This method is void and returns nothing. It's just used to do the
     * set-up of the rotor settings which plays a big role when it
     * comes to scrambling or encrypt the letters of the word you write
     */

    public void setFirstRotorSettings() {
        setRotorSettings(firstRotorSettings);
    }

    public void setSecondRotorSettings() {
        setRotorSettings(secondRotorSettings);
    }

    public void setThirdRotorSettings() {
        setRotorSettings(thirdRotorSettings);
    }

    private void setRotorSettings(Map<Character, Character> newRotorSettings) {
        while(true) {
            newRotorSettings.clear();
            int ascii = 'A';
            boolean error = false;

            System.out.println("Enter a rotor configuration that contains 26 letters! Don't repeat letters!");
            String rotorSettings = SCANNER.nextLine();

            if(rotorSettings.length() != 26) {
                System.err.println("Rotor configuration should contain 26 letters!");
                continue;
            }

            for(int i = 0; i < rotorSettings.length(); i++) {
                char letter = Character.toUpperCase(rotorSettings.charAt(i));

                if(letter < 'A' || letter > 'Z') {
                    System.err.println("Rotor configuration is wrong! Invalid characters. Type only letters!");
                    error = true;
                    break;
                }

                if(newRotorSettings.containsValue(letter)) {
                    System.err.println("Rotor configuration is wrong! Don't repeat letters!");
                    error = true;
                    break;
                }

                newRotorSettings.put((char) ascii++, letter);
            }

            if(!error) break;
        }
    }


    /**
     * Configures the plugboard connections
     * You can change the plugboard letter connections inside the method
     * <p>
     * This method is void and returns nothing. It's just used to do the
     * set-up of the plugboard letter connections, you can connect a letter
     * with another one. <b>The connection is optional</b>
     */
    public void setPlugboardSettings() {
        while(true) {
            plugboard.clear();
            boolean error = false;

            System.out.println("Enter words you would like to connect! Example : AB,CD (connects A<->B, C<->D)");
            String plugboardConf = SCANNER.nextLine();

            if(plugboardConf.length() > MAX_PLUGBOARD_LENGTH) {
                System.err.println("Plugboard's configuration length must be 26 or lower");
                continue;
            }

            for(int i = 0; i < plugboardConf.length(); i+=3) {
                char key = Character.toUpperCase(plugboardConf.charAt(i));
                char value = Character.toUpperCase(plugboardConf.charAt(i+1));

                if(key < 'A' || key > 'Z' || value < 'A' || value > 'Z') {
                    System.err.println("Plugboard's configuration is wrong! Invalid characters. Type only letters!");
                    error = true;
                    break;
                }

                if(plugboard.containsKey(key) || plugboard.containsKey(value)) {
                    System.err.println("Plugboard's configuration is wrong! Don't repeat letters!");
                    error = true;
                    break;
                }

                plugboard.put(key, value);
                plugboard.put(value, key);
            }

            if(!error) break;
        }
    }

    public void setReflectorSettings() {
        while(true) {
            reflector.clear();
            boolean error = false;

            System.out.println("Enter words you would like to connect! Example : AB,CD (connects A<->B, C<->D). Don't repeat letters!");
            String reflectorConf = SCANNER.nextLine();

            if(reflectorConf.length() != 38) {
                System.err.println("Reflector's configuration length must be 26");
                continue;
            }

            for(int i = 0; i < reflectorConf.length(); i+=3) {
                char key = Character.toUpperCase(reflectorConf.charAt(i));
                char value = Character.toUpperCase(reflectorConf.charAt(i+1));

                if(key < 'A' || key > 'Z' || value < 'A' || value > 'Z') {
                    System.err.println("Reflector's configuration is wrong! Invalid characters. Type only letters!");
                    error = true;
                    break;
                }

                if(reflector.containsKey(key) || reflector.containsKey(value)) {
                    System.err.println("Reflector's configuration is wrong! Don't repeat letters!");
                    error = true;
                    break;
                }

                reflector.put(key, value);
                reflector.put(value, key);
            }

            if(!error) break;
        }
    }

    public Map<Character, Character> getFirstRotorSettings() {
        return firstRotorSettings;
    }
    public Map<Character, Character> getSecondRotorSettings() {
        return secondRotorSettings;
    }
    public Map<Character, Character> getThirdRotorSettings() {
        return thirdRotorSettings;
    }

    public Map<Character, Character> getPlugboardSettings() {
        return plugboard;
    }

    public Map<Character, Character> getReflectorSettings() {
        return reflector;
    }

    public boolean anySettingEmpty() {
        return firstRotorSettings.isEmpty() || secondRotorSettings.isEmpty() || thirdRotorSettings.isEmpty();
    }
}