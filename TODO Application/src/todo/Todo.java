package todo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Todo {
  public static void main(String[] args) {
    String filename = "assets/tasks.txt";
    List<Task> tasks = new ArrayList<>();
    if (readFile(tasks, filename)) {

      if (args.length == 0) {
        System.out.println("Command Line Todo application\n" + "=============================\n");
        printUsage();
      }

      else if (args[0].equals("-l") || args[0].equals("-lu") || args[0].equals("-ld")) {
        if(args.length>1){
          System.out.println("-l/-lu/-ld has no other arguments!");
        }else {
          deleteTasks(tasks, args[0]);
          if (tasks.isEmpty()) {
            switch (args[0]) {
              case "-l": System.out.println("No todos for today! :)"); break;
              case "-lu": System.out.println("No undone tasks!"); break;
              case "-ld": System.out.println("No completed tasks!");
            }
          } else {
            listTasks(tasks);
          }
        }
      }

      else if (args[0].equals("-a")) {
        if (args.length > 1) {
            addNewTask(tasks, args);
            writeFile(tasks, filename);
        } else {
          System.out.println("Unable to add: no task provided!");
        }
      }

      else if (args[0].equals("-r")) {
        if(args.length==2 && (args[1].equals("-c") || args[1].equals("-uc"))){
          removeCheckedTasks(args,tasks);
          writeFile(tasks, filename);
        }
        else if (errorHandling(args, tasks, filename)) {
          removeTasks(args,tasks);
          writeFile(tasks, filename);
        }
      }

      else if (args[0].equals("-c") || args[0].equals("-uc")){
        if (errorHandling(args, tasks, filename)) {
          checkTasks(args,tasks);
          writeFile(tasks, filename);
        }
      }

      else if (args[0].equals("-del")) {
        if (args.length > 1) {
          System.out.println("-del has no other arguments!");
        } else {
          tasks.clear();
          writeFile(tasks, filename);
        }
      }

      else {
        System.out.println("Unsupported argument!\n");
        printUsage();
      }
    }
  }

  public static void printUsage() {
    System.out.println("Command line arguments:\n" + "\t-l     Lists all the tasks\n" +
        "\t-lu    Lists the undone tasks\n" + "\t-ld    Lists the done tasks\n" +
        "\t-a     Adds new tasks\n" + "\t-r     Removes tasks\n" + "\t-c     Completes tasks\n" +
        "\t-uc    Uncompletes tasks\n" + "\t-del   Delete every tasks\n\n"+
        "\t-r -c  Removes every completed tasks \n"+ "\t-r -uc Removes every uncompleted tasks");
  }

  private static void deleteTasks(List<Task> tasks, String arg) {
    for (int i = 0; i < tasks.size(); i++) {
      if (arg.equals("-lu") && tasks.get(i).isCompleted()) {
        tasks.remove(i);
        i--;
      }
      if (arg.equals("-ld") && !tasks.get(i).isCompleted()) {
        tasks.remove(i);
        i--;
      }
    }
  }

  public static void listTasks(List<Task> tasks) {
    for (int i = 0; i < tasks.size(); i++) {
      System.out.println(i + 1 + " - [" + (tasks.get(i).isCompleted() ? "x" : " ") +
          "] " + tasks.get(i).getDescription());
    }
  }

  public static void addNewTask(List<Task> tasks, String[] args) {
    for (int i = 1; i < args.length; i++) {
      tasks.add(new Task(args[i]));
    }
  }

  private static boolean errorHandling(String[] args, List<Task> tasks, String filename) {
    String keyWord = "";
    switch (args[0]) {
      case "-r": keyWord = "remove"; break;
      case "-c": keyWord = "check";break;
      case "-uc": keyWord = "uncheck";
    }
    if (args.length == 1) {
      System.out.println("Unable to " + keyWord + " tasks: no index provided!");
      return false;
    } else {
      for (int i = 1; i < args.length; i++) {
        if (!isStringInteger(args[i])) {
          System.out.println("Unable to " + keyWord + " tasks: some index is invalid!");
          return false;
        }
      }
      for (int i = 1; i < args.length; i++) {
        if (Integer.parseInt(args[i]) <= 0 || Integer.parseInt(args[i]) > tasks.size()) {
          System.out.println("Unable to " + keyWord + " tasks: some index is out of bound!");
          return false;
        }
      }
    }
    return true;
  }

  private static boolean isStringInteger(String index) {
    boolean isInteger = true;
    for (int i = 0; i < index.length(); i++) {
      if (!Character.isDigit(index.charAt(i))) {
        isInteger = false;
      }
    }
    return isInteger;
  }

  private static void removeTasks(String[] args, List<Task> tasks) {
    List<Integer> indexList = new ArrayList<>();
    for (int i = 1; i < args.length; i++) {
      if (!indexList.contains(Integer.parseInt(args[i])-1)) {
        indexList.add(Integer.parseInt(args[i]) - 1);
      }
    }
    Integer[] indexArray=new Integer[indexList.size()];
    indexArray=indexList.toArray(indexArray);
    Arrays.sort(indexArray);
    for (int i = 0; i < indexArray.length; i++) {
      tasks.remove((int)(indexArray[indexArray.length - i - 1]));
    }
  }

  private static void removeCheckedTasks(String[] args, List<Task> tasks) {
    boolean check=args[1].equals("-c");
    for (int i = 0; i <tasks.size(); i++) {
      if(tasks.get(i).isCompleted()==check){
       tasks.remove(i);
       i--;
      }
    }
  }

  private static void checkTasks(String[] args, List<Task> tasks) {
    boolean check = args[0].equals("-c");
    for (int i = 1; i < args.length; i++) {
      tasks.get(Integer.parseInt(args[i]) - 1).setCompleted(check);
    }
  }

  public static boolean readFile(List<Task> tasks, String filename) {
    boolean succeeded = false;
    try {
      FileInputStream fis = new FileInputStream(filename);
      if (fis.available() != 0) {
        ObjectInputStream is = new ObjectInputStream(fis);
        while (fis.available() != 0) {
          tasks.add((Task) is.readObject());
        }
        is.close();
      }
      succeeded = true;
    } catch (FileNotFoundException e) {
      System.out.println("Could not found the file!");
    } catch (IOException e) {
      System.out.println("Could not read the file!");
    } catch (ClassNotFoundException e) {
      System.out.println("Could not found the specific class int the file!");
    }
    return succeeded;
  }

  public static void writeFile(List<Task> tasks, String filename) {
    try {
      ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
      for (Task task : tasks) {
        os.writeObject(task);
      }
      os.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not found the file!");
    } catch (IOException e) {
      System.out.println("Could not write the file!");
    }
  }
}