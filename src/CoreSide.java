import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Created by GreMal on 07.02.2016.
 * В данном модуле собраны функции, которые вызываются внешним модулем с UI. Фактически - интерфейсные функции.
 */
public class CoreSide {
    /* Todo Tools.readFromIniFile(locFileName)*/

    /* Получить пару "ключ-значение" из iniData (сохраняемые данные между запусками программы)
    * Если искомый ключ отстутствует в iniData, то функция возвращает null */
    protected static String getPairFromIniDataMap(String key){
        if(Controller.model.iniData.containsKey(key)){
            return Controller.model.iniData.get(key);
        }
        return null;
    }
    /* Записать пару "ключ-значение" в iniData (сохраняемые данные между запусками программы)
    */
    protected static void putPairToIniDataMap(String key, String value){
        if(!getReady()){ return; }
        Controller.model.iniData.put(key, value);
    }

    // Получить статус готовности модели
    protected static boolean getReady(){ return Controller.model.getReady(); }

    protected static void setUIDisable(){ Controller.gui.setUIDisable(); };
    protected static void startSynchronization(){ Controller.model.startSynchronization(); };
    protected static void writeInit(){ Controller.model.writeInit(); };
    protected static void setUIEnable(){ Controller.gui.setUIEnable(); };

    protected static boolean isTextFieldFree(AbstractUIControl textField){ return Controller.model.isTextFieldFree(textField); }
    protected static boolean isWasChangedTrue(){ return Controller.model.isWasChangedTrue(); }
    protected static void EnterToCircleButtonPressed(){ Controller.model.EnterToCircleButtonPressed(); }
    protected static boolean isDeviceLabelEquals(AbstractUIControl auic){ return Controller.model.isDeviceLabelEquals(auic); }
    protected static void setDeviceLabelWasChangedFlagToTrue(AbstractUIControl textField){ Controller.model.setDeviceLabelWasChangedFlagToTrue(textField); }
    protected static boolean isNoteEquals(String str){ return isNoteEquals(str); }
    protected static void setNoteWasChangedFlagToTrue(){ Controller.model.setNoteWasChangedFlagToTrue(); }
    protected static void InviteOrKickButtonPressed(AbstractUIControl button){ Controller.model.InviteOrKickButtonPressed(button); }

    protected static Map<String, String> readFromIniFile(String fileName) throws FileNotFoundException, IOException { return Tools.readFromIniFile(fileName); }
    // Получить ссылку на объект ЛогФайла
    protected static FileHandler getLogFileHandler(){ return Controller.handler;  }
    protected static Level getLogLevel(){ return Controller.LOG_LEVEL; }
}
