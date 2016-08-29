/*
Здесь используется парадигма мультиагентного программирования.(для реализации была выбрана  библиотека JADE, Java Agent Developing Framework).
Данный файл  представляет собой агента, который получает данные и добавляет в базу данных аукциона новую ставку.
Кроме того, в данном коде производится запуск мультиагентной платформы и контейнеров.
*/
package auctionServer;

import inprocess.*;
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import jade.wrapper.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.Arrays;


public class DataBaseAgent {

  
  public static class CondVar {
    private boolean value = false;

    synchronized void waitOn() throws InterruptedException {
      while(!value) {
	wait();
      }
    }

    synchronized void signal() {
      value = true;
      notifyAll();
    }

  } 


 
   public static class DbEditAgent extends jade.core.Agent {
    Object array;
     private Connection connect = null;
                
                private PreparedStatement preparedStatement = null;
                
    public void setup() {
      
      setEnabledO2ACommunication(true, 10);
       Object[] args = getArguments();
      if(args.length > 0) {
	CondVar latch = (CondVar)args[0];
	latch.signal();
      }
      Object  objj;
      
        addBehaviour(new jade.core.behaviours.CyclicBehaviour() {

	public void action() {
             
	 
	  int [] obj;
             obj = new  int[2];
             obj = (int[]) getO2AObject();
          
         if(obj != null) {
	    //System.out.println("Got an object from the queue: [" + obj + "]");
            //array= obj;
            System.out.println("--------------------------------------------------------------------------------");
           int maxMoney = (int)obj[0];
            for (int i=0;i < 3 ;i++) {
                System.out.println(obj[i]);
            }
	  try {
                            
                            Class.forName("com.mysql.jdbc.Driver");
                            connect = DriverManager.getConnection("jdbc:mysql://localhost/auction?"
                                    + "user=lidia&password=123");

                            
                            String query = "insert into auction.stakes(lot_id, user_id, stake_date, stake_size) " + 
                                    "values(?, ?, ?, ?)";
                            preparedStatement = connect.prepareStatement(query);
                            preparedStatement.setInt(1,  15);
                            preparedStatement.setInt(2,8);
                            preparedStatement.setDate(3, new java.sql.Date(2015, 12, 12));
                            preparedStatement.setInt(4,maxMoney);
                            preparedStatement.executeUpdate();
                        } catch (ClassNotFoundException ex) {
                        java.util.logging.Logger.getLogger(DataBaseAgent.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        java.util.logging.Logger.getLogger(DataBaseAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
          
             
             
          
          
          }
	  else 
	    block();
	}
        
        

      });
      addBehaviour(new jade.core.behaviours.OneShotBehaviour() {

	public void action() {
            }
        

      });
      
      
          
      
    }

    public void takeDown() {
     
      setEnabledO2ACommunication(false, 0);
    }

  } 

  public String LaunchAgent(int islucky,int userId,int maxMoney ) {
    
      //System.out.println("Выбранная стратегия :" + p1+" id пользователя :" + p2+"  максимальная величина ставки" + maxMoney);
    int [] objArr;
      objArr = new int[3];
      objArr[0] = islucky;
      objArr[1] = userId;
      objArr[2] = maxMoney;
      for (int i = 0;i < 3;i++ ) {
          System.out.println(objArr[i]);
      }
             
     
      try {

      // Запускаем JADE runtime
      Runtime rt = Runtime.instance();

      // Выйти из JVM, если больше нет контейнеров  
      rt.setCloseVM(true);

     

      // Запускаем платформу на  8888 порту
      // Создаем профиль по умолчанию 
      Profile pMain = new ProfileImpl(null, 8888, null);

      System.out.println("Запускаем всю платформу (in-process)..."+pMain);
      AgentContainer mc = rt.createMainContainer(pMain);

      //Создаем профиль по умолчанию, чтобы запустить контейнер 
      
      ProfileImpl pContainer = new ProfileImpl(null, 8888, null);
      System.out.println("Запускаем контейнер для агентов..."+pContainer);
      AgentContainer cont = rt.createAgentContainer(pContainer);
      //System.out.println(pContainer);

      System.out.println("Создаем rma агента на платформе ...");
      AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
      rma.start();
      
      //запускаем агента-покупателя (AgentBuyer)
      //данный агент принимает параметры через канал связи, который называется  от-объекта-к-агенту (object-to-agent)
       //используется метод putO2AObject

     

      CondVar startUpLatch = new CondVar();

      AgentController custom = mc.createNewAgent("AgentBuyer", DbEditAgent.class.getName(), new Object[] { startUpLatch });
      custom.start();

      // Ждем, пока агент не запустится и не получит обьект
      try {
	startUpLatch.waitOn();
      }
      catch(InterruptedException ie) {
	ie.printStackTrace();
      }
	    

      // Вставляем объект в очередь , асинхронно.
      System.out.println("Вставляем объект в очередь , асинхронно....");
      custom.putO2AObject(objArr, AgentController.ASYNC);
      System.out.println("Обьект добавлен в очередь.");
     }
    catch(Exception e) {
      e.printStackTrace();
    }

  String varForServerWork ="varForServerWork";
return varForServerWork;
}
}
