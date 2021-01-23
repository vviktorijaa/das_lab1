import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/*
 * Креирање html фајл кој се прикажува при повик на Servlet - кликање на копче
 * Search ги прикажува растојанијата до најблиските ресторани
 */
class CreateHtml {
	public void createHtml() throws IOException {
		File f = new File("distance.html");

		BufferedWriter bw = new BufferedWriter(new FileWriter(f));

		File file = new File("rastojanija.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();

		bw.write("<html><body style=\"height: 97%;"
				+ "background-image: url(slikiApp/pozadina_mapa_burred.jpg); background-size: cover; background-repeat: no-repeat;"
				+ "font-family: 'Trebuchet MS', sans-serif\">"
				+ "<a href=\"home.html\"><img src=\"slikiApp/levo_strelka.png\" style=\"float:left; height:15%; width:10%; cursor:pointer\"></a><img style=\"display:block; margin-left:auto; margin-right:auto; width: 30%\" id=\"logoDistance\" src=\"slikiApp/logo_shadow.png\"><div id=\"distanceContainer\""
				+ "style=\"margin:auto; margin:auto; height:65%; width:50%; padding:10px\" id=\"distanceContainer\">\r\n");
		 
		while (line != null) {
			//System.out.println("LINE" + line);
			bw.write("<p style=\"font-size:3vw; margin-left:10px\">" + line
					+ "</p><div style=\"margin-top: -95px; margin-left:475px\"><img onclick=\"addToVisited()\" style=\"height:16%; width:26%; cursor:pointer\" src=\"slikiApp/pin_shadow.png\"><img onclick=\"addToFaves()\" style=\"height:16%; width:37%; cursor:pointer\" src=\"slikiApp/favourites.png\"></div>");
			bw.write("<br>");
			line = br.readLine();
		}
		br.close();
		bw.write("</div></body></html>");

		bw.close();
	}
}

public class MyServletDemo extends HttpServlet {

	public void mainFunc(String v) throws IOException {
		File file = new File("database.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		SLL<Double> lista = new SLL();

		String[] pomNiza = v.split(" ");

		double lat = Double.parseDouble(pomNiza[0]);
		double lon = Double.parseDouble(pomNiza[1]);
		double currLat = 0;
		double currLon = 0;
		String line = br.readLine();

		while (line != null) {
			if (line.contains("lat")) {
				currLat = Double.parseDouble(line.substring(4));
			}
			if (line.contains("lon")) {
				currLon = Double.parseDouble(line.substring(4));
			} else if (currLat != 0 && currLon != 0) {
				double distance = measure(lat, lon, currLat, currLon);
				lista.insertLast(distance);

				currLat = 0;
				currLon = 0;
			}
			line = br.readLine();
		}
		br.close();

		vratiNajkratki(lista);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String value = (String) request.getParameter("pole1");
		String [] razdeli=value.split("\\W+");
		for(int i=0; i<value.length(); i++) {
			if(Character.isLetter((value.charAt(i)))) {
				String nextHTML = "/home.html?t=\" + System.currentTimeMillis()"; //home
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextHTML);
				dispatcher.forward(request, response);
			}
		}
		if (value.isEmpty() || razdeli.length==1) {
			String nextHTML = "/home.html?t=\" + System.currentTimeMillis()"; //home
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextHTML);
			dispatcher.forward(request, response);
		}
		else {
			mainFunc(value);

			CreateHtml html = new CreateHtml();
			html.createHtml();

			String nextHTML = "/distance.html?t=\" + System.currentTimeMillis()";
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextHTML);
			dispatcher.forward(request, response);
		}
		// response.sendRedirect("http://.google.com");F
	}

	/*
	 * Пресметува растојание помеѓу координатите внесени од корисникот и
	 * координатите на сите ресторани од базата
	 */
	public static double measure(double lat1, double lon1, double lat2, double lon2) {
		int R = 6371; // Радиус на планетата Земја во километри
		double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
		double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;

		return d * 1000; // метри
	}

	/*
	 * Ги враќа трите најкратки растојанија од внесените координати до 3те најблиски
	 * ресторани и растојанијата (во метри) ги запишува во txt фајл
	 */
	public void vratiNajkratki(SLL<Double> lista) throws IOException {
		SLLNode<Double> d1 = lista.getFirst();
		double min1 = d1.element;
		double min2 = d1.element;
		double min3 = d1.element;

		d1 = d1.succ;

		while (d1 != null) {
			if (d1.element.compareTo(min1) < 0) {
				min1 = d1.element;
			} else if (d1.element.compareTo(min2) < 0) {
				min2 = d1.element;
			} else if (d1.element.compareTo(min3) < 0) {
				min3 = d1.element;
			}
			d1 = d1.succ;
		}

		try {
			FileWriter myWriter = new FileWriter("rastojanija.txt");

			DecimalFormat df = new DecimalFormat("#.##");
			String formatted1 = df.format(min1);
			myWriter.write(formatted1 + " meters away");
			System.out.println("MIN1: " + formatted1);

			myWriter.write("\n");

			String formatted2 = df.format(min2);
			myWriter.write(formatted2 + " meters away");
			System.out.println("MIN2: " + formatted2);

			myWriter.write("\n");

			String formatted3 = df.format(min3);
			myWriter.write(formatted3 + " meters away");
			System.out.println("MIN3: " + formatted3);
			myWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}

//Помошна податочна структура
class SLLNode<E> {
	protected E element;
	protected SLLNode<E> succ;

	public SLLNode(E elem, SLLNode<E> succ) {
		this.element = elem;
		this.succ = succ;
	}

	@Override
	public String toString() {
		return element.toString();
	}
}

class SLL<E> {
	private SLLNode<E> first;

	public SLL() {
		this.first = null;
	}

	@Override
	public String toString() {
		String ret = new String();
		if (first != null) {
			SLLNode<E> tmp = first;
			ret += tmp + "->";
			while (tmp.succ != null) {
				tmp = tmp.succ;
				ret += tmp + "->";
			}
		} else
			ret = "Prazna lista!!!";
		return ret;
	}

	public void insertFirst(E o) {
		SLLNode<E> ins = new SLLNode<E>(o, first);
		first = ins;
	}

	public void insertLast(E o) {
		if (first != null) {
			SLLNode<E> tmp = first;
			while (tmp.succ != null)
				tmp = tmp.succ;
			SLLNode<E> ins = new SLLNode<E>(o, null);
			tmp.succ = ins;
		} else {
			insertFirst(o);
		}
	}

	public SLLNode<E> getFirst() {
		return first;
	}
}