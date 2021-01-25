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

	GlavnaFunkcija g=new GlavnaFunkcija();
	DecimalFormat df = new DecimalFormat("#.##");

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();
		
		String value = (String) request.getParameter("pole1");
		String [] razdeli=value.split("\\W+");
		for(int i=0; i<value.length(); i++) {
			if(Character.isLetter((value.charAt(i)))) {
				String nextHTML = "/home.html?t=\"";
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextHTML);
				dispatcher.forward(request, response);
			}
		}
		if (value.isEmpty() || razdeli.length==1) {
			String nextHTML = "/home.html?t=\"";
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextHTML);
			dispatcher.forward(request, response);
		}
		else {
			out.print("<html><body style=\"height: 97%;"
					+ "background-image: url(slikiApp/pozadina_mapa_burred.jpg); background-size: cover; background-repeat: no-repeat;"
					+ "font-family: 'Trebuchet MS', sans-serif\">"
					+ "<a href=\"home.html\"><img src=\"slikiApp/levo_strelka.png\" style=\"float:left; height:15%; width:10%; cursor:pointer\"></a><img style=\"display:block; margin-left:auto; margin-right:auto; width: 30%\" id=\"logoDistance\" src=\"slikiApp/logo_shadow.png\"><div id=\"distanceContainer\""
					+ "style=\"margin:auto; margin:auto; height:65%; width:50%; padding:10px\" id=\"distanceContainer\">\r\n"+"<p style=\" text-shadow: 2px 2px #D5D5D5; font-size: 3vw;  text-align: justify\"><b>"+df.format(g.mainFunc(value))+" meters away from the nearest restaurant in your area.");
		}
	}
}


class GlavnaFunkcija{
	
	PretvoriVoMetri pretvoriVoMetri=new PretvoriVoMetri();
	VratiNajbliskiRestorani najbliski=new VratiNajbliskiRestorani();
	
	public double mainFunc(String v) throws IOException {
		File file = new File("database.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		SLL<Double> lista = new SLL<>();

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
				double distance = pretvoriVoMetri.measure(lat, lon, currLat, currLon);
				lista.insertLast(distance);

				currLat = 0;
				currLon = 0;
			}
			line = br.readLine();
		}
		br.close();

		return najbliski.vratiNajblizok1(lista);
	}
}

class PretvoriVoMetri{
	public double measure(double lat1, double lon1, double lat2, double lon2) {
		int R = 6371; 
		double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
		double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;

		return d * 1000;
	}
}

class VratiNajbliskiRestorani{
	public double vratiNajblizok1(SLL<Double> lista) {
		SLLNode<Double> d1 = lista.getFirst();
		double min1 = d1.element;

		d1 = d1.succ;

		while (d1 != null) {
			if (d1.element.compareTo(min1) < 0) {
				min1 = d1.element;
			}
			d1 = d1.succ;
		}

		//DecimalFormat df = new DecimalFormat("#.##");
		//String formatted1 = df.format(min1);
		//System.out.println("MIN1: " + formatted1);

		return min1;
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