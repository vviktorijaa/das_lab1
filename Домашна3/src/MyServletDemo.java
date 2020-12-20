import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class MyServletDemo extends HttpServlet {

	public void mainFunc(String v) throws IOException {
		File file = new File("C:\\Users\\Viki\\Desktop\\database.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		DecimalFormat df = new DecimalFormat("#.##");
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

		PrintWriter out = response.getWriter();

		String value = (String) request.getParameter("pole1");

		mainFunc(value);

		CreateHtml html = new CreateHtml();
		html.createHtml();

		String nextHTML = "/distance.html";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextHTML);
		dispatcher.forward(request, response);
	}

	public static double measure(double lat1, double lon1, double lat2, double lon2) {
		int R = 6371; // Radius of earth in KM
		double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
		double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;

		return d * 1000; // metri
	}

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

			myWriter.write("\n");

			String formatted2 = df.format(min2);
			myWriter.write(formatted2 + " meters away");

			myWriter.write("\n");

			String formatted3 = df.format(min3);
			myWriter.write(formatted3 + " meters away");

			myWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}

class CreateHtml {
	public void createHtml() throws IOException {
		File f = new File("C:/Users/Viki/eclipse-workspace/Application/WebContent/distance.html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));

		File file = new File("rastojanija.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();

		bw.write("<html><body style=\"background: linear-gradient(-45deg, #545353, #7e7a7a, #a5a7ae, #d4cfcf);"
				+ "width:100%; height: 100%; background-size: cover; margin-left: auto;  margin-right: auto;"
				+ "font-family: 'Trebuchet MS', sans-serif\">"
				+ "<a href=\"pochetna.html\"><img src=\"slikiApp/levo_strelka.png\" style=\"float:left; height:15%; width:10%; cursor:pointer\"></a><img style=\"display:block; margin-left:auto; margin-right:auto; width:25%; height:25%\" id=\"logoDistance\" src=\"slikiApp/logo_shadow.png\"><div id=\"distanceContainer\" style=\"border:1px solid black; width:70%;"
				+ "margin:auto; height:65%; margin-top:10px\" id=\"distanceContainer\">\r\n");

		while (line != null) {
			bw.write("<p style=\"font-size:3vw; margin-left:10px\">" + line + "</p><div style=\"margin-top: -95px; width:100%; margin-left:475px\"><img onclick=\"addToVisited()\" style=\"height:16%; width:5%; cursor:pointer\" src=\"slikiApp/pin_shadow.png\"><img onclick=\"addToFaves()\" style=\"height:16%; width:7%; cursor:pointer\" src=\"slikiApp/favourites.png\"></div>");
			bw.write("<br>");
			line = br.readLine();
		}
		br.close();
		bw.write("</div></body></html>");

		bw.close();
	}
}

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
		// Construct an empty SLL
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
