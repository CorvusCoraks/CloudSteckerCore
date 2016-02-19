import javax.swing.*;

/**
 * Created by GreMal on 07.02.2016.
 */
public interface UISide {
    /* Функция проверки связи для вызова из внешних модулей. Формирование статусной строки в GUI,
    запуск процедур активации и деактивации элементов GUI */
    public void setInternetConnectionStatuses(InternetConnectionTest.InternetConnectionMessage status);
    /* Функция читает планируемые параметры GUI из соответствующего Мэпа контроллера */
    public void setInitGUIParameters();
    // Просто тестовая функция. Не нужна в работе.
    //public void testFunction();
    /* чтение из файла данных локализации (язык интерфейса). Имеет делать общий файл локализации для всех UI? */
    //public void localisation();
    public boolean getReady();
    // Получить ссылку на текстовое поле с именем данного устройства
    public AbstractUIControl getThisDeviceTextField();
    //public void setThisDeveceTextField(String DeveceName);
    // Получить ссылку на кнопку "Сохранить" название данного устройства
    public AbstractUIControl getThisDeviceButton();
    // Получить массив ссылок на текстовые поля других устройств круга
    public AbstractUIControl[] getOtherCircleDevicesTextField();
    // получить массив ссылок на кнопки "Пригласить - Выгнать"
    public AbstractUIControl[] getOtherCircleDevicesButton();
    // получить ссылку на кнопку "Синхронизировать"
    //public JButton getSynchronisationButton();
    // Получить ссылку на текстовую область с заметкой
    public AbstractUIControl getNoteTextArea();
    // возвращает ссылку на текстовое поле по ссылке на спаренную кнопку
    public AbstractUIControl getTextPaired(AbstractUIControl button);
    // Ссылка на текстовое поле с пригласительным паролем
    public AbstractUIControl getInvitationTextField();
    // возвращает свободное текстовое поле, в GUI-таблице устройств круга
    public AbstractUIControl getFreeOtherDeviceTextField();
    // возвращает ссылку на кнопку по парному текстовому полю
    public AbstractUIControl getButtonByTextField(AbstractUIControl auic);
    // возвращает ссылку на текстовое поле, по парной кнопке
    //public JTextField getTextFieldByButton(JButton btn);
    // инвертируем текст кнопок Kick/Invite
    public void invertTextOnButton(AbstractUIControl auic);
    /*    Чистка свободного текстового поля в GUI-массиве устройств круга и, если на парной кнопке осталась старая
        надпись Kick, меняем её на Invite */
    public void clearFreeTextField();
    /* Сделать доступным элементы окна программы. Реализация должна быть synchronized */
    //public void setFrameEnable();
    /* Сделать недоступными элементы окна программы. Реализация должна быть synchronized */
    //public void setFrameDisable();
    /* Функция возвращает статус активности окна приложения. Для поределения используется состояние noteArea */
    //public boolean isFrameEnabled();
    // вставить новый статус в массив статусов
    public void putNewStatusInStatusString(StatusSender sender, String status);
    // вставить новый статус в массив статусов, с указанием количества показов.
    // После указанного количества показов, статус удаляется из массива показов.
    //public void putNewStatusInStatusString(Tools.StatusSender sender, String status, int showCount);
    // удалить статус из массива статусов
    //public void removeStatusFromStatusString(Tools.StatusSender sender);
    public String getLocalisationValueByKey(String key);
}
