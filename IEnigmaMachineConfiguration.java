import java.util.Map;

public interface IEnigmaMachineConfiguration {
    boolean anySettingEmpty();
    void setFirstRotorSettings();
    void setSecondRotorSettings();
    void setThirdRotorSettings();
    void setPlugboardSettings();
    void setReflectorSettings();

    Map<Character, Character> getFirstRotorSettings();
    Map<Character, Character> getSecondRotorSettings();
    Map<Character, Character> getThirdRotorSettings();
    Map<Character, Character> getPlugboardSettings();
    Map<Character, Character> getReflectorSettings();
}
