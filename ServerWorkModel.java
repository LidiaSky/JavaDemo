/*

ServerWork Model.Задача выполнена в рамках предмета оценка производительности вычислительных систем.
Постановка задачи:
В вычислительную систему поступает поток заданий InStream. Интервал времени между поступлениями заданий с номерами i – 1 и i равен i . Задание i требует времени i для его обработки сервером S, причем задание полностью занимает все ресурсы вычислительной системы с начала его обработки и до окончания. Задания, приходящие в систему в то время, когда сервер занят обработкой предыдущих заданий, становятся в очередь (буфер) Q, характеризующуюся одной из дисциплин (FIFO, LIFO, RAND) и предельной длиной – величиной буфера (при полной занятости накопителя задания теряются). Обработанные задания образуют выходящий из системы поток OutStream  (интервалы времени между выходящими заданиями обозначим i).
Величины i и i считаются случайными, взаимно независимыми с распределениями вероятностей A(x) и B(x) соответственно. Другие условия:
	Поток	Сервер S	Буфер Q	Примечание
		

	E3	R	K	2	FIFO
Требуется: Разработать математическую и имитационную модели описанных процессов, провести исследование характеристик компьютерной системы методом имитационного моделирования.
Исследование, проводимое помощью моделирования, включает получение следующих сведений:	

	Q	S	OutStream
	W(x)	Pпростоя	 Aout(x)
*/

package performance.evaluation;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.Chart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import static performance.evaluation.Erlang.Erlang;
import static performance.evaluation.RandomRange.GenerateRandomValue;

/**
 *
 * @author Лидия
 */
public class ServerWorkModel {
    
    
    public static int Erlang(int start,int end)
        {
        int alfa = 1;
        double Umultipl = 1; 
       
        for (int i = 0;i <3;i++) {
            Umultipl *= GenerateRandomValue(start,end);
        }
        double Erlang =  alfa * Math.log(Umultipl);
        Erlang = (int)Math.round(Erlang);
        return (int)Erlang;
        }
    
    public final class RandomRange {
  
  public   int GenerateRandomValue(int start,int end){
   
    
    int START = start;
    int END = end;
    Random random = new Random();
   
    int randValue = showRandomInteger(START, END, random);
    
    
    
    return randValue;
  }
  
  public  int showRandomInteger(int aStart, int aEnd, Random aRandom){
    if (aStart > aEnd) {
      throw new IllegalArgumentException("Start cannot exceed End.");
    }
    
    long range = (long)aEnd - (long)aStart + 1;
    
    long fraction = (long)(range * aRandom.nextDouble());
    int randomNumber =  (int)(fraction + aStart);    
     return (randomNumber);
  }
    }

    
    
   public static void main (String [] args) throws IOException {
  //генерируем интервал времени между поступлениями заданий
   
    Scanner in = new Scanner(System.in);
       
     int   alpha = 1;
    //генерируем время обработки задания 
     System.out.println("Введите диапазон для генерации времени обработки задания сервером (равномерное расперделение)");
        int start = in.nextInt();
        int end = in.nextInt();
        int R; 
     
      int sysTime = 0;//Системное время
      int Tobr = 0;//оставшееся время обработки заявки в сервере
      int serverBusyTime =0;//время занятости сервера
      int Entered = 0;//счетчик заявок поступивших на сервер
      int Done = 0;//Счетчик заявок, обработанных сервером
      int queueLen = 0;
      int requestInterval = 0; 
      int pIDLE = 0;
      LinkedList<Integer> qq = new LinkedList<Integer>();
      LinkedList<Integer> wait = new LinkedList<Integer>();//время ожидания каждой заявки в очереди
      LinkedList<Integer> omega = new LinkedList<Integer>();//сколько заявок ждало n секунд, где n-индекс элеменвот массива
      LinkedList<Integer> fnomega = new LinkedList<Integer>();//функция распределения
      
      System.out.println("Введите количество заданий,которые необходимо обработать:");
      int tasksNumber = in.nextInt();
      while (Done != tasksNumber) {
        //если у нас одно задание еще находится на выполнении
         if (Tobr >0) {
             Tobr -=1;
             //если вдруг на этом этапе задание выполнилось, то увеличиваем число выполненных заданий
             if(Tobr ==0) {
                 Done+=1;
                
             }}
              //есди же сервер свободен, но в очереди есть заявка
             if ((queueLen >0) && (Tobr ==0)){
                 queueLen-=1;
                 
                 Entered +=1;
                 //qq - это наша очередь
                 Tobr = qq.get(0);
                 qq.remove(0);
               
             
                 
             }
         
         //новоприбывшая заявка
         if (requestInterval ==0) {
             //генерируем время обработки
             R = GenerateRandomValue(start,end);
             //генерируем время, через которое поступит новый клиент
             requestInterval = Erlang(1,10);
             //нинциализируем соответствующий элемент массива wait
             wait.addLast(0);
             //если сервер свободен, генерируем новое время обработки и увиличиваем число поступивших заявок
             if((Tobr ==0)&&(queueLen == 0)){
                 Tobr = R;
                 Entered += 1;
                 
             //если же сервер занят, то добавляем заявку в очередю, вернее время выполнения     
             }else if (queueLen < 2) {
                 queueLen += 1;
                 qq.addLast(R);
                 if (queueLen > 0) { 
                     System.out.println("Очередь есть.Длина " + queueLen);
                 }
             }
         //моделируем , что прошел один тик
         } else if (requestInterval >0){
            requestInterval= requestInterval-1;
      }
         sysTime+=1;
         
         if(Tobr == 0) {
             pIDLE +=1;
             
         }
         for (int i=Entered;i < wait.size();i++) {
             wait.set(i, wait.get(i)+1);
         }
         
         
         }
      int max=0;
      for (int i =0;i < wait.size();i++) {
      if (wait.get(i)>max) max=wait.get(i);
      }
     for (int i =0;i <= max;i++) {
         omega.addLast(0);
     }
      for (int i =0;i < wait.size();i++) {
          omega.set(wait.get(i), omega.get(wait.get(i))+1);
      // System.out.print(wait.get(i));
      }
       for (int i =0;i < omega.size();i++) {
         // omega.set(wait.get(i), omega.get(wait.get(i))+1);
       System.out.print(omega.get(i));
      }
        for (int i =0;i < omega.size();i++) {
         // omega.set(wait.get(i), omega.get(wait.get(i))+1);
       System.out.println(omega.get(i));
      }
        
      System.out.println("Системное время = " + sysTime);
      fnomega.addLast(omega.get(0));
      for(int i =1;i<omega.size();i++) {
       fnomega.addLast(fnomega.get(i-1)+omega.get(i));
   }
    for(int i =1;i<fnomega.size();i++) {
       fnomega.set(i,fnomega.get(i)/fnomega.getLast());
   }
    for(int i =1;i<fnomega.size();i++) {
       System.out.println(fnomega.get(i));
   }
    System.out.println("Вероятность простоя =" + pIDLE/sysTime);
    double[] xData = new double[] { 0.0,1.0,2,3 };
    double[] yData = new double[] { 0.0,1.0,4,9 };

    // Create Chart
    Chart chart = QuickChart.getChart("W(X)", "X", "Y", "y(x)", xData, yData);

    // Show it
    new SwingWrapper(chart).displayChart();

    // Save it
    BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);

    // or save it in high-res
    BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);

    // Save it
   // BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);

    // or save it in high-res
    //BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);
   }
}
   
   
