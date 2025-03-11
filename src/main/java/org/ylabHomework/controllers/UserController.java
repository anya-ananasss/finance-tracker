package org.ylabHomework.controllers;


import lombok.Getter;
import lombok.Setter;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.UserService;

import java.util.List;
import java.util.Scanner;

/**
 * Контроллер для сущности User и взаимодействия с приложением через консольный интерфейс.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 07.03.2025
 * </p>
 */
@Getter
@Setter
public class UserController {
    /**
     * Конструктор для создания контроллера с заданным сервисом.
     *
     * @param service сервис с бизнес-логикой для User
     */
    public UserController(UserService service) {
        this.service = service;


        service.createUser("admin", "admin@ya.ru", "imcooladmin");
        service.readUserByEmail("admin@ya.ru").setRole(0);
        service.createUser("anya", "anya@ya.ru", "1234");
        service.readUserByEmail("anya@ya.ru").getTransactions().add(new Transaction(Transaction.TransactionTYPE.INCOME, 2500.0, "Стипендия", "ура!"));
        service.readUserByEmail("anya@ya.ru").getTransactions().add(new Transaction(Transaction.TransactionTYPE.EXPENSE, 250.0, "Карамельный макиато", "необходимость"));
    }

    private final Scanner scanner = new Scanner(System.in);
    private final UserService service;
    private User loggedUser = null;
    private boolean mainPageShown = false;

    /**
     * Показывает приветственный экран, где выводит меню выбора действий:
     * зарегистрироваться, войти в аккаунт или выйти из программы.
     */
    public void showGreetingScreen() {
        mainLoop:
        while (true) {
            if (!mainPageShown) {
                System.out.println("Здравствуйте! Введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти из программы");
                setMainPageShown(true);
            }
            String i = scanner.nextLine();
            switch (i) {
                case "1" -> {
                    registerNewUser();
                    System.out.println("Введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти из программы");
                }
                case "2" -> {
                    loginUser();
                    break mainLoop;
                }
                case "3" -> {
                    exitApp();
                    break mainLoop;
                }

                default ->
                        System.out.println("Пожалуйста, введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти!");
            }
        }
    }

    /**
     * Регистрирует нового пользователя, проверяя приемлемость нового имени, адреса электронной почты и совпадение
     * повторно введенного пароля.
     */
    public void registerNewUser() {
        setMainPageShown(false);
        System.out.println("=РЕГИСТРАЦИЯ=");
        String name = enterNameInRegistration();
        String email = enterEmailInRegistration();
        String password = enterPasswordInRegistration();
        try {
            service.createUser(name, email, password);
            System.out.println("Регистрация прошла успешно!");
        } catch (Exception e) {
            System.out.println("Ошибка при регистрации! " + e.getMessage());
        }
        showGreetingScreen();
    }

    /**
     * Проверяет, не пусто ли имя, которое пользователь вводит при регистрации. Если пусто, просит ввести имя еще раз,
     * пока оно не будет непустым.
     *
     * @return имя пользователя
     */
    public String enterNameInRegistration() {
        String name;
        while (true) {
            System.out.println("Введите свое имя");
            name = scanner.nextLine();
            String state = service.nameCheck(name);
            if (!state.equals("Имя не может быть пустым! Пожалуйста, введите имя!")) {
                break;
            } else {
                System.out.println(state);
            }
        }
        return name;
    }

    /**
     * Проверяет приемлемость адреса электронной почты, который пользователь вводит при регистрации. Если этот адрес уже
     * зарегистрирован или не соответствует шаблону адреса электронной почты, просит ввести адрес еще раз, пока он не будет
     * приемлемым.
     *
     * @return адрес электронной почты пользователя
     */
    public String enterEmailInRegistration() {
        String email;
        while (true) {
            System.out.println("Введите адрес электронной почты:");
            email = scanner.nextLine();
            String state = service.emailCheck(email);
            if (!state.equals("Пожалуйста, введите корректный email!")
                    && !state.equals("Пользователь с таким email уже зарегистрирован!")) {
                break;
            } else {
                System.out.println(state);
            }
        }
        return email;
    }

    /**
     * Проверяет адрес электронной почты, вводимой при логине. Если введен адрес, не соответствующий шаблону адреса
     * электронной почты, просит ввести еще раз, если адрес корректен, но не зарегистрирован, предлагает попробовать
     * ввод еще раз или вернуться на главную страницу.
     *
     * @return адрес электронной почты
     */
    public String enterEmailInLogin() {
        String email;
        while (true) {
            System.out.println("Введите адрес электронной почты:");
            email = scanner.nextLine();
            String state = service.emailCheck(email);
            if (state.equals("Пользователь с таким email уже зарегистрирован!")) {
                break;
            } else if (!state.equals("Пожалуйста, введите корректный email!")) {
                System.out.println("Пользователь с таким email не найден!");
                System.out.println("Нажмите 1, чтобы попробовать еще раз, 2, чтобы вернуться в меню.");
                String i = scanner.nextLine();
                switch (i) {
                    case "1" -> {
                    }
                    case "2" -> showGreetingScreen();
                    default -> System.out.println("Введите 1 или 2.");
                }
            } else {
                System.out.println(state);
            }
        }
        return email;
    }

    /**
     * Проверяет пароль, вводимый при регистрации. Если повторный пароль не совпадает с изначально введенным, просит
     * ввести еще раз.
     *
     * @return пароль пользователя
     */
    public String enterPasswordInRegistration() {
        String password;
        while (true) {
            System.out.println("Введите пароль");
            password = scanner.nextLine();
            System.out.println("Повторите пароль");
            String repeatedPass = scanner.nextLine();
            if (!password.equals(repeatedPass)) {
                System.out.println("Пароли не совпадают! Пожалуйста, повторите попытку!");
            } else {
                break;
            }
        }
        return password;
    }

    /**
     * Производит логин пользователя. Если пройдена проверка адреса электронной почты, но пользователь с ней заблокирован -
     * выводит сообщение о блокировке аккаунта и возвращает на главную страницу. Если пройдена проверка адреса и пользователь
     * активен - показывает главную страницу для пользователя, если пользователь имеет роль "1" - обычный пользователь,
     * или страницу для администратора, если пользователь имеет роль "0" - администратор. Если пользователь с введенным
     * адресом электронной почты не найден, возвращает на главную страницу.
     */
    public void loginUser() {
        setMainPageShown(false);
        System.out.println("=ВХОД=");
        String email = enterEmailInLogin();
        if (service.emailCheck(email).equals("Пользователь с таким email уже зарегистрирован!")) {
            if (service.readUserByEmail(email).isActive()) {
                enterPasswordInLogin(email);
                loggedUser = service.readUserByEmail(email);
                if (service.readUserByEmail(email).getRole() == 1) {
                    System.out.println("Здравствуйте, " + loggedUser.getName() + "!");
                    showMainPageUser();
                } else {
                    showMainPageAdmin();
                }
            } else {
                System.out.println("К сожалению, ваш аккаунт заблокирован! Обратитесь к администратору.");
                showGreetingScreen();
            }
        } else {
            System.out.println("Пользователь с таким email не найден!");
            showGreetingScreen();
        }
    }

    /**
     * Проверяет соответствие введенного при логине пароля настощяему паролю пользователя. Устанавливает loggedUser,
     * если соответствие присутствует, иначе просит повторить ввод до ввода верного пароля или предлагает вернуться на главный экран.
     *
     * @param email адрес электронной почты пользователя, который входит в аккаунт.
     */
    public void enterPasswordInLogin(String email) {
        String password;
        while (true) {
            System.out.println("Введите пароль");
            password = scanner.nextLine();
            UserService.LoginResult loginResult = service.loginUser(email, password);
            if (loginResult.success()) {
                loggedUser = loginResult.user();
                System.out.println("Авторизируем...");
                break;
            } else {
                System.out.println("Введен неверный пароль! Повторите попытку!");
                System.out.println("Нажмите 1, чтобы попробовать еще раз, 2, чтобы вернуться в меню.");
                String i = scanner.nextLine();
                switch (i) {
                    case "1" -> {
                    }
                    case "2" -> showGreetingScreen();
                    default -> System.out.println("Введите 1 или 2.");
                }
            }
        }
    }

    /**
     * Показывает главную страницу для пользователя.
     * Можно перейти к трекингу финансов, в личный кабинет или выйти из программы.
     */
    public void showMainPageUser() {
        System.out.println("Нажмите 1, чтобы перейти к трекингу финансов, 2 - чтобы перейти в личный кабинет, 3 - чтобы выйти из программы");
        mainLoop:
        while (true) {
            String j = scanner.nextLine();
            switch (j) {
                case "1" -> {
                    goToTransactionsController();
                    break mainLoop;
                }
                case "2" -> {
                    showPersonalAccountSettings();
                    break mainLoop;
                }
                case "3" -> exitApp();
                default ->
                        System.out.println("Пожалуйста, нажмите 1, чтобы перейти к трекингу финансов, 2 - чтобы перейти в личный кабинет, 3 - чтобы выйти из программы!");
            }
        }
    }

    /**
     * Показывает страницу личного кабинета.
     * Можно изменить личную информацию, выйти из аккаунта, удалить аккаунт или вернуться к выбору страницы.
     */
    public void showPersonalAccountSettings() {
        System.out.println("Введите 1, чтобы изменить личную информацию, 2 - чтобы выйти из аккаунта, 3 - чтобы удалить аккаунт, 4 - вернуться назад");
        mainLoop:
        while (true) {
            String i = scanner.nextLine();
            switch (i) {
                case "1" -> {
                    updateUser();
                    break mainLoop;
                }
                case "2" -> {
                    logoutUser();
                    break mainLoop;
                }
                case "3" -> {
                    deleteUser();
                    break mainLoop;
                }
                case "4" -> {
                    showMainPageUser();
                    break mainLoop;
                }
                default ->
                        System.out.println("Пожалуйста, введите 1, чтобы изменить личную информацию, 2 - чтобы выйти из аккаунта, 3 - чтобы удалить аккаунт, 4 - вернуться назад!");
            }
        }
    }

    /**
     * Показывает страницу для изменения личной информации.
     * Можно поменять имя, адрес электронной почты или пароль.
     */
    public void updateUser() {
        System.out.println(Constants.UPDATE_USER_TEXT_BLOCK);
        mainLoop:
        while (true) {
            String i = scanner.nextLine();
            switch (i) {
                case "1" -> {
                    updateName();
                    showPersonalAccountSettings();
                    break mainLoop;
                }
                case "2" -> {
                    updateEmail();
                    showPersonalAccountSettings();
                    break mainLoop;
                }
                case "3" -> {
                    updatePass();
                    showPersonalAccountSettings();
                    break mainLoop;
                }
                default ->
                        System.out.println("Пожалуйста, введите цифру 1, чтобы изменить имя пользователя, 2, чтобы изменить email или 3, чтобы изменить пароль!");
            }
        }
    }

    /**
     * Обеспечивает выход пользователя из системы.
     */
    public void logoutUser() {
        setLoggedUser(null);
        showGreetingScreen();
    }

    /**
     * Обновляет пароль пользователя, проверяя правильность повторного ввода пароля.
     */
    public void updatePass() {
        String updatedValue;
        while (true) {
            System.out.println("Введите старый пароль");
            String exPass = scanner.nextLine();
            if (service.comparePass(exPass, loggedUser.getEmail())) {
                System.out.println("Введите новый пароль");
                updatedValue = scanner.nextLine();
                service.updatePassword(updatedValue, loggedUser.getEmail());
                System.out.println("Пароль успешно обновлен!");
                break;
            } else {
                System.out.println("Неправильный пароль! Повторите попытку!");
            }
        }
    }

    /**
     * Обновляет адрес электронной почты пользователя.
     * Проверяет его на приемлемость и выводит сообщение об успешном обновление, если адрес приемлем, иначе - сообщение
     * о том, почему не удалоось обновить адрес.
     */
    public void updateEmail() {
        String updatedValue;
        while (true) {
            System.out.println("Введите новый адрес электронной почты");
            updatedValue = scanner.nextLine();
            String res = service.updateEmail(updatedValue, loggedUser.getEmail());
            if (res.equals(updatedValue)) {
                System.out.println("Адрес электронной почты обновлен успешно! Новый адрес: " + updatedValue);
                break;
            } else {
                System.out.println(res);
            }
        }
    }

    /**
     * Обновляет имя пользователя.
     * Проверяет его на приемлемость и выводит сообщение об успешном обновлении, если имя приемлемо, иначе - сообщение
     * о том, почему не удалоось обновить имя.
     */
    public void updateName() {
        String updatedValue;
        while (true) {
            System.out.println("Введите новое имя");
            updatedValue = scanner.nextLine();
            String res = service.updateName(updatedValue, loggedUser.getEmail());
            if (res.equals(updatedValue)) {
                System.out.println(updatedValue + ", имя изменено успешно!");
                break;
            } else {
                System.out.println(res);
            }
        }
    }

    /**
     * Удаляет пользователя после его повторного подтверждения, после - показывает приветственный экран;
     * Возвращает в личный кабинет, если повторное подтверждение не было получено.
     */
    public void deleteUser() {
        mainLoop:
        while (true) {
            System.out.println("Вы действительно хотите удалить свой аккаунт? Это действие невозможно отменить! да/нет");
            String i = scanner.nextLine().toLowerCase();
            switch (i) {
                case "да" -> {
                    service.deleteUserByEmail(loggedUser.getEmail());
                    setLoggedUser(null);
                    System.out.println("Аккаунт успешно удален.");
                    showGreetingScreen();
                    break mainLoop;
                }
                case "нет" -> {
                    showPersonalAccountSettings();
                    break mainLoop;
                }
                default -> System.out.println("Введите да или нет.");
            }
        }
    }

    /**
     * Показывает главную страницу для администратора.
     * Можно просмотреть всех пользователей, заблокировать, разблокировать или удалить пользователя по его email.
     */
    public void showMainPageAdmin() {
        System.out.println("Здравствуйте, Администратор!");
        mainLoop:
        while (true) {
            System.out.println(Constants.SHOW_ADMIN_MAIN_PAGE_TEXT_BLOCK);
            String i = scanner.nextLine();
            switch (i) {
                case "1" -> {
                    if (service.getAllUsers().size() == 1) {
                        System.out.println("Ни одного пользователя не создано!");
                    } else {
                        List<User> users = service.getAllUsers();
                        users.remove(service.readUserByEmail(loggedUser.getEmail()));
                        int num = 1;
                        for (User user : users) {
                            System.out.println(num + ". " + user.getName() + ", " + user.getEmail() + ", активен=" + user.isActive());
                        }
                    }
                }
                case "2" -> {
                    if (service.getAllUsers().size() == 1) {
                        System.out.println("Ни одного пользователя не создано!");
                    } else {
                        System.out.println("Введите email пользователя, которого хотите заблокировать.");
                        String email = scanner.nextLine();
                        if (service.emailCheck(email).equals("Пользователь с таким email уже зарегистрирован!")) {
                            if (!service.readUserByEmail(email).isActive()) {
                                System.out.println("Пользователь уже заблокирован.");
                            } else {
                                service.readUserByEmail(email).setActive(false);
                                service.updateActive(false, email);
                                System.out.println("Успешно");
                            }
                        } else {
                            System.out.println("Пользователь с таким email не найден!");
                        }
                    }
                }
                case "3" -> {
                    if (service.getAllUsers().size() == 1) {
                        System.out.println("Ни одного пользователя не создано!");
                    } else {
                        System.out.println("Введите email пользователя, которого хотите разблокировать.");
                        String email = scanner.nextLine();
                        if (service.emailCheck(email).equals("Пользователь с таким email уже зарегистрирован!")) {
                            if (service.readUserByEmail(email).isActive()) {
                                System.out.println("Пользователь не заблокирован.");
                            } else {
                                service.readUserByEmail(email).setActive(true);
                                service.updateActive(true, email);
                                System.out.println("Успешно");
                            }
                        } else {
                            System.out.println("Пользователь с таким email не найден!");
                        }
                    }
                }
                case "4" -> {
                    if (service.getAllUsers().size() == 1) {
                        System.out.println("Ни одного пользователя не создано!");
                    } else {
                        System.out.println("Введите email пользователя, которого хотите удалить.");
                        String email = scanner.nextLine();
                        service.deleteUserByEmail(email);
                        System.out.println("Успешно");
                    }
                }
                case "5" -> {
                    logoutUser();
                    break mainLoop;
                }
                default -> System.out.println("Введите 1, 2, 3, 4 или 5.");
            }
        }
    }

    /**
     * Завершает выполнение программы.
     */
    public void exitApp() {
        System.exit(0);
    }

    /**
     * Переносит пользователя на страницу для управления его транзакциями.
     */

    private void goToTransactionsController() {
        TransactionRepository transRepository = new TransactionRepository(this.loggedUser);
        TransactionService transService = new TransactionService(transRepository, this.loggedUser);
        TransactionController transController = new TransactionController(transService, this, this.loggedUser);
        transController.showMainMenu();
    }
}