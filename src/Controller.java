/**
 * Created by GreMal on 21.02.2015.
 */

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/*
* Компания - устройства, участвующие в обслуживании данной конкректной заметки (одного конкретного пользователя
*
* Если в директории программы отсутствуе файл Start.jar, значит обновление программы через Start.jar не требуется
* (например, это делается другим способом, через магазин приложений). В общем, другим путём.
*
* Так же, это удобно в случае отладки программы через IDE
* */

/* Todo Сделать отдельный поток, в котором будет периодически проверяться состояние флагов в объекте GUI. Перед этим, обязательная проверка на существование объекта GUI. Если объекта GUI нет - поток завершается */
public class Controller {
    protected final static String DEFAULT_DATA_STAMP = "0000-00-00 00:00:00";
    protected final static float PROGRAM_VERSION = 0.02f;
    protected final static int MAX_CHARS_IN_LABEL = 25;
    protected final static int MAX_CHARS_IN_NOTE = 1000; // максимальное количество символов в заметке
    protected final static int CHARS_IN_INVITATION_PASS = 5; // количество символов в пригласительном пароле
    protected final static String OS_NAME = System.getProperty("os.name");
    protected final static String LAST_VER_FILE_LOCATION = "http://cn.gremal.ru/files/lastver/cloudsticker.zip";
    protected static TestInternetConnectionThread jerkThread;
    //protected List<ConnectedElements> connectionsModelWithGUI;
    //private Controller controller;
    protected static GUI gui = null;
    protected static Model model = null;
    // файл создаётся с первоначальными настройками во время инсталляции программы

    // Настройки файлов лога
    private static final String LOGFILE_PATTERN = "./start%g.log"; // имя лог-файла
    private static final int LOGFILE_SIZE = 10000; // размер одного лог-файла
    private static final int LOGFILES_NUMBER = 5; // количество лог-файлов
    private static final boolean LOGFILE_APPEND = true;
    protected static final Level LOG_LEVEL = Level.FINE; // уровень сообщений лога
    protected static Level LOG_TRIGGER = Level.OFF; // включаем отключаем логирование
    protected static FileHandler handler;
    // Карта для накопления статусов, пока ещё не готово GUI


    // Key - deviceId, Value - deviceLabel
    //protected static Map<String, String> devic    esInCircle = new HashMap<String, String>();

    //static DeviceInfo thisDeviceInfo = new DeviceInfo();
    //protected static String thisDeviceId;
    //protected static String thisUserId;

    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        handler = new FileHandler(LOGFILE_PATTERN, LOGFILE_SIZE, LOGFILES_NUMBER, LOGFILE_APPEND);
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(LOG_TRIGGER);
        handler.publish(new LogRecord(LOG_LEVEL, "Input in Controller.main"));

        // обновление файла start.jar
        File oldStartFile = new File("./Start.jar");
        File newStartFile = new File("./new_start.jar");
        Boolean isStartFilePresent = false;
        if (oldStartFile.exists()){isStartFilePresent = true;}

        // Если Start.jar присутствует, работаем с попыткой обновления программы
        if (isStartFilePresent) {
            if (newStartFile.exists()) {
                oldStartFile.delete();
                newStartFile.renameTo(oldStartFile);
            }

            // Если запуск программы произошёл не через start.jar, то перекидываем управление принудительно в start.jar
            //System.out.println("на входе в main");
            List<String> argsList = Arrays.asList(args);
            if (!argsList.contains("start")) {
                //System.out.println("внтутри Контроллера");
            /* ------------------------------------------------------------- */
                Process proc = Runtime.getRuntime().exec("java -jar Start.jar");
                return;
            /* ------------------------------------------------------------- */
            }
        }
        //Internet.Result result = Internet.getNote("hkZ6gBomOdh6o9cX","D4gGlTVoScqSfvXZ");

        //boolean connected = Model.isInternerConnectionActive();
        /* Сначала модель и ГУИ создаются БЕЗ взаимных связей, до полной сборки самих себя.
         * Только после этого устанавливаются связи. */

        /* Так как, не равенство null ссылок на gui и model НЕ означает, что их формирование и инициализация завершены,
        * надо в этих объектах завести поля, которые будут true ПОСЛЕ полной инициализации этих объектов
        *
        * Вызов констурктора gui не означает, что в следующей строке gui будет не null
        */
        model = new Model();
        handler.publish(new LogRecord(LOG_LEVEL, "Controller. Create model/"));

        // чтение файла ini
        model.readInit();
        handler.publish(new LogRecord(LOG_LEVEL, "Controller. readInit"));

        // Первоначальный запуск нити проверки связи с Интернет
        jerkThread = new TestInternetConnectionThread(gui);
        jerkThread.start();
        handler.publish(new LogRecord(LOG_LEVEL, "Controller. Start JerkThread"));

        handler.publish(new LogRecord(LOG_LEVEL, "Controller. gui: " + gui));
        // handler.publish(new LogRecord(LOG_LEVEL, "Controller. GUI class: " + GUI.class.toString()));
        // запуск GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //handler.publish(new LogRecord(LOG_LEVEL, "Controller.run. GUI {: " + gui.toString()));
                gui = new GUI();
                if (gui == null){ /* todo сделать выдачу предупреждающего окна */return; }
                handler.publish(new LogRecord(LOG_LEVEL, "Controller.run. GUI }: " + gui.toString()));
            }
        });

        // Задержка, чтобы позволить GUI полностью сформироваться до инициализации модели
        handler.publish(new LogRecord(LOG_LEVEL, "Controller.Next. Wait GUI Ready. Gui: " + gui));
        while (gui == null) { Thread.sleep(10); }
        while (!gui.getReady()) { Thread.sleep(10); }
        handler.publish(new LogRecord(LOG_LEVEL, "Controller.main. GUI: "+gui.toString()));

        // запуск нити, которая будет контролировать состояние флагов UI
        new Thread(){
            @Override
            public void run(){
                //super.run();
                try {
                    while (!gui.isCloseWindowCommand()) {
                        CommunicationChannal channal;
                        // Проверка флагов и соответствующая реакция на них
                        if(gui.getJerkThreadWakeUpCommand()){Controller.jerkThread.controller.wakeUp();}
                        else{Controller.jerkThread.controller.pause();}
                        if(gui.isEnterToCircleButtonPressed()){
                            model.EnterToCircleButtonPressed();
                            gui.CoreOK_EnterToCircleButtonPressed();
                        }
                        if(gui.isStartSynchronizationCommand()){
                            model.startSynchronization();
                            gui.CoreOK_StartSynchronizationCommand();
                        }
                        AbstractUIControl InviteOrKickButton = gui.InviteOrKickButtonPressed();
                        if(InviteOrKickButton != null){
                            //model.startSynchronization();
                            Controller.model.InviteOrKickButtonPressed(InviteOrKickButton);
                            gui.CoreOK_InviteOrKickButtonPressed();
                        }
                        /* todo получение сигнала об изменении метки устройства */
                        /* todo получение сигнала об изменении заметки */
                        /* todo получение сигнала на выдачу массива со ссылками занятых текстовых полей */

                        sleep(10);
                    }
                    if(gui.isCloseWindowCommand()){
                        // Дана команда на закрытие приложения
                        model.startSynchronization();
                        model.writeInit();
                        gui.setUIPaused(false);
                    }
                }catch (InterruptedException ignore){/* NOP */}
            }
        }.start();

        /*
        {
            @Override
            public void run(){
                while (!isCloseWindowCommand){
                    try {
                        this.sleep(10);
                    }catch (InterruptedIOException ignore ){}
}
}
        }
        */
        // инициализация данных
        handler.publish(new LogRecord(LOG_LEVEL, "Controller.Next. Model.Initialisation."));
        model.initialization();

        // model.isMODELready = true;

        // Первоначальный запуск нити проверки связи с Интернет
/*        jerkThread = new TestInternetConnectionThread(gui);
        jerkThread.start();*/

        /* Проверка наличия новой версии программы на сервере (номер последней версии хранится в файле lastver.txt)
        * и скачивание новой версии при необходимости */
        /* Версия является десятичной дробью */
/*
         if(InternetConnectionTest.isCloudReachable() == InternetConnectionTest.InternetConnectionMessage.YES) {
             boolean isRefreshNeeded = false;
            Internet.Result answer = Internet.getLastProgramVer();
            String ver = (String) answer.content;
            if (!ver.equals("null") && !ver.equals(PROGRAM_VERSION)) {
                gui.putNewStatusInStatusString(GUI.StatusSender.CONTROLLER, "New version CloudSticker present. Please, update!", 10);
                File fileName = new File("./lastversion.jar");
                // if(fileName.exists()){ fileName.delete(); }
                // Если файл новоё версии уже есть в каталоге программы, то скачиваеть обновление не следует.
                if (!fileName.exists()) {
                    byte[] fileContent = Internet.getLastVerCloudNotes(LAST_VER_FILE_LOCATION);
                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                    fileOutputStream.write(fileContent);
                    fileOutputStream.close();
                }
            }
        }*/


        // Скачивание обновлённой версии
        if (isStartFilePresent) {
            if (InternetConnectionTest.isCloudReachable() == InternetConnectionTest.InternetConnectionMessage.YES) {
                boolean isRefreshNeeded = false;
                Internet.Result answer = Internet.getLastProgramVer();
                float ver = Float.parseFloat((String) answer.content);
                if (PROGRAM_VERSION < ver) {
                    //gui.putNewStatusInStatusString(GUI.StatusSender.CONTROLLER, "New version CloudSticker ready.", 10);
                    File fileName = new File("./cloudsticker.zip");
                    // Если файл новоё версии уже есть в каталоге программы, то скачиваеть обновление не следует.
                    if (!fileName.exists()) {
                        byte[] fileContent = Internet.getLastVerCloudNotes(LAST_VER_FILE_LOCATION);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                        fileOutputStream.write(fileContent);
                        fileOutputStream.close();
                    }
                }
            }
        }

        // Передача статистики
        if(InternetConnectionTest.isCloudReachable() == InternetConnectionTest.InternetConnectionMessage.YES) {
            Map<String, String> hash = new HashMap<String, String>();
            hash.put("os", OS_NAME);
            Internet.Result answer = Internet.sendStatistics(hash);
/*            String ver = (String) answer.content;
            if (!ver.equals("null") && !ver.equals(PROGRAM_VERSION)) {
                gui.putNewStatusInStatusString(GUI.StatusSender.CONTROLLER, "New version CloudSticker present. Please, update!", 10);
            }*/
        }

        handler.close();
    }
}
