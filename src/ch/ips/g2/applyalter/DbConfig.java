package ch.ips.g2.applyalter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import org.xml.sax.InputSource;

import ch.ips.base.BaseXMLDeserializer;


/**
 * Databases configuration with method applied to all (or used) database instances
 * @author Martin Caslavsky &lt;martin.caslavsky@ips-ag.cz&gt;
 * @version $Id$
 */
public class DbConfig
{
  protected List<DbInstance> d;
  
  /**
   * fail with first exception or collect them and report at one 
   */
  protected boolean ignorefailures;
  
  @SuppressWarnings("unchecked")
  public DbConfig(String file, boolean ignorefailures) {
    try {
      d = (List<DbInstance>) new BaseXMLDeserializer().fromXML(new InputSource(new FileInputStream(file)));
    } catch (FileNotFoundException e) {
      throw new ApplyAlterException("File not found " + file, e);
    }
    if (d == null)
      throw new ApplyAlterException("Unable to deserialize DbConfig from file " + file);
    for (DbInstance i: d)
      i.getConnection();
    this.ignorefailures = ignorefailures;
  }
  
  public List<DbInstance> getEntries() {
    return d;
  }
 
  public boolean isIgnorefailures()
  {
    return ignorefailures;
  }

  /**
   * Close connections to all database instances
   */
  public void closeConnections() {
    for (DbInstance i: d)
      i.closeConnection();
  }

  /**
   * Commit used connections
   * @param ignorefailures 
   * @see DbInstance#useConnection() 
   * @see DbInstance#isUsed() 
   * @throws ApplyAlterException if one or some of connection can not be commited
   */
  public void commitUsed() throws ApplyAlterException
  {
    ApplyAlterExceptions aae = new ApplyAlterExceptions();
    for (DbInstance i: d) {
      if (i.isUsed())
        try {
          System.out.println("Commiting " + i.getId());
          i.getConnection().commit();
        } catch (SQLException e) {
          ApplyAlterException ex = new ApplyAlterException("Error commiting", e);
          if (ignorefailures) aae.add(ex);
          else throw ex;
        }
    }
    if (!aae.isEmpty())
      throw aae;
  }


}
