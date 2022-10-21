import Logic.BotUseCase;
import Logic.TaskManager;

public class Bot {
    private TaskManager taskManager;

    public Bot(BotUseCase router) {
        taskManager = new TaskManager(router);
    }

    public void add(String text) {
        taskManager.addNewTask(text);
    }

    public void showList() {
        taskManager.showList();
    }

    public void mark(String i,  boolean value){
        int index = Integer.parseInt(i);
        taskManager.markTask(index - 1, value);
    }

    public void delete(String number) {
        int index = Integer.parseInt(number);
        taskManager.remove(index - 1);
    }

    public void goodbye() {
        taskManager.goodbye();
    }
}
