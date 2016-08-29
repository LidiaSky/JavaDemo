/*
После получения ACL-сообщения c запросом, которое содержит в себе <имя-удаленной-платформы>,<адрес-удаленной-платформы> 
производится перемешение агента на указанную платформу.
После перемещения, агент выполняет простое действие, создание файла.
*/
import jade.core.AID;
import jade.core.Agent;
import jade.core.PlatformID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Лидия
 */
public class InterPMigration extends Agent{
    protected void setup() {
        
        addBehaviour(new CyclicBehaviour(this){
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if (msg != null) {
                    //мы ожидаем содержимое в виде <имя-удаленной-платформы>,<адрес-удаленной-платформы>
                    String str = msg.getContent();
                    String[] remotePlatformInfo = str.split(",");
                    if (remotePlatformInfo == null || remotePlatformInfo.length != 2) {
						System.out.println("WRONG content");
					}
					else {
						System.out.println("Initiating migration to remote platform "+remotePlatformInfo[0]+" at address "+remotePlatformInfo[1]);
						AID remoteAMS = new AID("ams@"+remotePlatformInfo[0], AID.ISGUID);
						remoteAMS.addAddresses(remotePlatformInfo[1]);
						PlatformID dest = new PlatformID(remoteAMS);
						doMove(dest);
					}
				}
				else {
					block();
				}
			}
		});
	}
	
	public void afterMove() {
		System.out.println("Just arrived in platform "+getAMS().getHap());
                System.out.println("Yes it works!!!");
                File newFile = new File("newfile.txt");
            try {
                if (newFile.createNewFile()) {
                    System.out.println("Новый файл создан");
                } else {
                    System.out.println("Файл уже существует");
                }
            } catch (IOException ex) {
                Logger.getLogger(MigrationAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
}


                    
