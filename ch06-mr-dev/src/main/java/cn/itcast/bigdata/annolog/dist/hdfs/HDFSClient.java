package cn.itcast.bigdata.annolog.dist.hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

class HDFSClient {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		// 发送文件
		File file = new File("c:/access.log");
		FileInputStream fi1 = new FileInputStream(file);
		
		//定义文件分块大小为1M
		long blockSize = 1* 1024 * 1024;
		
		//TODO  还可加入分块逻辑

		// 请求slave1,发送第1块
		Socket socket1 = new Socket("127.0.0.1", 988);
		OutputStream out1 = socket1.getOutputStream();
		long count=0;
		int b;
		while((b=fi1.read())!=-1){
			out1.write(b);
			count ++;
			if(count==blockSize) break;
		}
		out1.flush();
		out1.close();
		socket1.close();

		// 请求slave2,发送第2块
		Socket socket2 = new Socket("127.0.0.1", 987);
		OutputStream out2 = socket2.getOutputStream();
		count=0;
		while((b=fi1.read())!=-1){
			out2.write(b);
		}
		out2.flush();
		out2.close();
		socket2.close();

//		Thread.sleep(Long.MAX_VALUE);
	}

}
