package todo;

import java.io.Serializable;

public class Task implements Serializable {
  private boolean completed;
  private String description;

  public Task(String description) {
    this.completed = false;
    this.description = description;
  }
  public Task(boolean completed,String description){
    this.completed=completed;
    this.description=description;
  }

  public boolean isCompleted() {
    return completed;
  }

  public String getDescription() {
    return description;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
}
