public class Main {
    public static void main(String[] args) {
        EnigmaMachine e = new EnigmaMachine();
        String encryptedWord = e.decrypt();
        System.out.println(encryptedWord);
    }
}
