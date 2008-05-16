package ch.ips.g2.applyalter;
import java.io.PrintStream;


public class ApplyAlterException extends RuntimeException
{

  public ApplyAlterException() {
  }

  public ApplyAlterException(String message) {
    super(message);
  }

  public ApplyAlterException(Throwable cause) {
    super(cause);
  }

  public ApplyAlterException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public void printMessages(PrintStream out) {
    Throwable e = this;
    do {
      out.println(e.getMessage());
      e = e.getCause();
    } while (e != null);
  }

}
