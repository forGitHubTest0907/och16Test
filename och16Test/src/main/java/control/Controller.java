package control;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.CommandProcess;

/**
 * Servlet implementation class Controller
 */
// @WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 목적 --> init으로 xml의 command.properties을 읽어서 commandMap 등록
	private Map<String, Object> commandMap = new HashMap<String, Object>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// web.xml 에서 propertyConfig 에 해당하는 init-param 의 값을 읽어옴
		String props = config.getInitParameter("config");
		System.out.println("1. init String props=> "+ props);
		Properties 		pr = new Properties();
		FileInputStream f = null;
		
		try {
			String configFilePath = config.getServletContext().getRealPath(props);
			System.out.println("2. init String configFilePath=> "+ configFilePath);
			// String props -> file 변신
			f = new FileInputStream(configFilePath);
			
			// Memory Up
			pr.load(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (f != null) 
				try {
					f.close();
				} catch (IOException ex) {}
		}
		Iterator keyIter = pr.keySet().iterator();
		
		while (keyIter.hasNext()) {
			String command = (String) keyIter.next();
			// service.ListAction
			String className = pr.getProperty(command);
			System.out.println("3. init command=> "+command);	//	/och16/com
			System.out.println("4. init className=> "+className);	
			
			// ListAction listAction = new ListAction();
			try {
				// 문자열 className -> service.ListAction 가 class 로 변신
				Class<?> commandClass = Class.forName(className);
				// 클래스로부터, service.ListAction 가 Instance 로 변신
				CommandProcess commandInstance = 
						(CommandProcess) commandClass.getDeclaredConstructor().newInstance();
				commandMap.put(command, commandInstance);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	//해당 문자열을 클래스로 만든다
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		requestServletPro(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		requestServletPro(request, response);
	}
	
	protected void requestServletPro(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String view = null;
		CommandProcess com = null;
		String command = request.getRequestURI();
		System.out.println("1. requestServletPro command=> "+ command); 	//	/ch16/list.do
		command = command.substring(request.getContextPath().length());
		System.out.println("2. requestServletPro command substring=> "+ command); 	//	/ch16/com
		
		try {
			//  service.ListAction Instance		commandMap에서 해당하는 CommandProcess 인스턴스를 가져옵니다.
			com  = (CommandProcess) commandMap.get(command);
			System.out.println("3. requestServletPro command=> "+ command); 	//	/ch16/com
			System.out.println("4. requestServletPro com=> "+ com); 	//	/ch16/com
			
			// 해당 커맨드를 실행하고 뷰 페이지 정보를 받아옵니다.
			//	com --> service.ListActon@32a22787
			view = com.requestPro(request, response);
			System.out.println("5. requestServletPro view=> "+ view); 	//	/ch16/com
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		// Ajax Or NOT Ajax 아작스냐 아니냐
		// 1. Ajax 이면: 나 부른 놈 화면 안에 데이터만 집어넣는 역할
		if (command.contains("ajaxGet")) {
			System.out.println("ajaxGet String->"+command);
			String writer = (String) request.getAttribute("writer");
			// 공식 -> 사용자 브라우저에 보여주는 객체
			PrintWriter out = response.getWriter();	//PrintWriter 화면에 뿌려주는 객체
			out.write(writer);
			out.flush();	//버퍼에 남은 게 있으면 강제 출력
			
		// 2. Or NOT -> 일반 command(Service)	
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher(view);
			dispatcher.forward(request, response);
		}
	}

}
