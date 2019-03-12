package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
 
 
public class main {
    private static final short K=8;     //使用常量来定义，方便之后解N皇后问题
    private static int N=0;
    static File file = new File("/Users/taohuadao/Documents/WFE/N-queen/src/main/Nvalue");
	static String[] split=txt2String(file).split(" ");
    public static void main(String[] args) throws Exception {
    	int n=split.length;
        for(int j=1;j<n;j++){
        	N=Integer.valueOf(split[j]);
            long count=0;
            Date begin =new Date();
            /**
             * 初始化棋盘，使用一维数组存放棋盘信息
             * chess[n]=X:表示第n行X列有一个皇后
             */
             
            List<short[]> chessList=new ArrayList<short[]>(N);
            for(short i=0;i<N;i++){
                short chess[]=new short[N];
                chess[0]=i;
                chessList.add(chess);
            }
             
            short taskSize =(short)( N/2+(N%2==1?1:0) );
            // 创建一个线程池
            ExecutorService pool = Executors.newFixedThreadPool(taskSize);
            // 创建多个有返回值的任务
            List<Future<Long>> futureList = new ArrayList<Future<Long>>(taskSize);
            for (int i = 0; i < taskSize; i++) {
                Callable<Long> c = new QueenThread(chessList.get(i));
                // 执行任务并获取Future对象
                Future<Long> f = pool.submit(c);
                futureList.add(f);
            }
            // 关闭线程池
            pool.shutdown();
             
            for(short i=0; i<(short) (taskSize - (N%2==1?1:0)); i++){              
                count+=futureList.get(i).get();
            }
            count=count*2;
            if(N%2==1)
                count+=futureList.get(N/2).get();
             
            Date end =new Date();
            System.out.println("解决 " +N+ "皇后问题，用时：" +String.valueOf(end.getTime()-begin.getTime())+ "毫秒，计算结果："+count);
        }
    }
    public static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
    public static short[] convertToShort(int i)   
    {   
        short[] a=new short[2];   
        a[0] = (short) (i & 0x0000ffff);          //将整型的低位取出,   
        a[1] = (short) (i >> 16);                     //将整型的高位取出.   
        return a;   
    }   
}
 
class QueenThread implements Callable<Long>{
    private short[] chess;
    private short N;
     
    public QueenThread(short[] chess){
        this.chess=chess;
        this.N=(short) chess.length;
    }
     
     
    @Override
    public Long call() throws Exception {
        return putQueenAtRow(chess, (short)1) ;
    }
 
 
    private Long putQueenAtRow(short[] chess, short row) {
        if(row==N){
            return (long) 1;
        }
         
        short[] chessTemp=chess.clone();
        long sum=0;
        /**
         * 向这一行的每一个位置尝试排放皇后
         * 然后检测状态，如果安全则继续执行递归函数摆放下一行皇后
         */
        for(short i=0;i<N;i++){
            //摆放这一行的皇后
            chessTemp[row]=i;
             
            if( isSafe( chessTemp,row,i) ){
                sum+=putQueenAtRow(chessTemp,(short) (row+1));
            }
        }
         
        return sum;
    }
     
    private static boolean isSafe(short[] chess,short row,short col) {
        //判断中上、左上、右上是否安全
        short step=1;
        for(short i=(short) (row-1);i>=0;i--){
            if(chess[i]==col)   //中上
                return false;
            if(chess[i]==col-step)  //左上
                return false;
            if(chess[i]==col+step)  //右上
                return false;
           
            step++;
        }
         
        return true;
    }

}