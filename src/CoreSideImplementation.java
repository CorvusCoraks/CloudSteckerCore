import ru.gremal.cs.common.interfaces.CoreSide;
import ru.gremal.cs.common.ui.AbstractUIControl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Created by GreMal on 07.02.2016.
 * В данном модуле собраны функции, которые вызываются внешним модулем с UI. Фактически - интерфейсные функции.
 */
public class CoreSideImplementation implements CoreSide {
    /* Todo Tools.readFromIniFile(locFileName)*/

    //int temp = SLEEP_INTERVAL;
    /* Получить пару "ключ-значение" из iniData (сохраняемые данные между запусками программы)
    * Если искомый ключ отстутствует в iniData, то функция возвращает null */
    @Override
    public String getPairFromIniDataMap(String key){
        if(!getReady()){ return null; }
        if(Controller.model.iniData.containsKey(key)){
            return Controller.model.iniData.get(key);
        }
        return null;
    }
    /* Записать пару "ключ-значение" в iniData (сохраняемые данные между запусками программы)
    */
    @Override
    public void putPairToIniDataMap(String key, String value){
        if(!getReady()){ return; }
        Controller.model.iniData.put(key, value);
    }

    // Получить статус готовности модели
    @Override
    public boolean getReady(){ return Controller.model.getReady(); }
    // protected void setReady(boolean value){ Con }
    protected static void setUIDisable(){ Controller.gui.setUIDisable(); };
    @Override
    public void startSynchronization() throws InterruptedException { Controller.model.startSynchronization(); };
    @Override
    public void writeInit(){ Controller.model.writeInit(); };
    protected static void setUIEnable(){ Controller.gui.setUIEnable(); };
    @Override
    public boolean isTextFieldFree(AbstractUIControl textField){ return Controller.model.isTextFieldFree(textField); }
    @Override
    public boolean isWasChangedTrue(){ return Controller.model.isWasChangedTrue(); }
    @Override
    public void enterToCircleButtonPressed(){ Controller.model.EnterToCircleButtonPressed(); }
    protected static boolean isDeviceLabelEquals(AbstractUIControl auic){ return Controller.model.isDeviceLabelEquals(auic); }
    @Override
    public void setDeviceLabelWasChangedFlagToTrue(AbstractUIControl textField){ Controller.model.setDeviceLabelWasChangedFlagToTrue(textField); }
    protected static boolean isNoteEquals(String str){ return isNoteEquals(str); }
    @Override
    public void setNoteWasChangedFlagToTrue(){ Controller.model.setNoteWasChangedFlagToTrue(); }
    @Override
    public void inviteOrKickButtonPressed(AbstractUIControl button){ Controller.model.InviteOrKickButtonPressed(button); }

   // protected static Map<String, String> readFromIniFile(String fileName) throws FileNotFoundException, IOException { return Tools.readFromIniFile(fileName); }
    // Получить ссылку на объект ЛогФайла
    @Override
    public FileHandler getLogFileHandler(){ return Controller.handler;  }
    @Override
    public Level getLogLevel(){ return Controller.LOG_LEVEL; }
    @Override
    public float getCoreVersion(){
        return Controller.PROGRAM_VERSION;
    }
    @Override
    public void jerkThreadWakeUp(){Controller.jerkThread.controller.wakeUp();}
    @Override
    public void jerkThreadSleep(){Controller.jerkThread.controller.pause();}
    @Override
    public boolean isJerkThreadActive(){return Controller.jerkThread.controller.isPause();}
}
