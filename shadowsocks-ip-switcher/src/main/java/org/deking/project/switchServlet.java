package org.deking.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/update")
public final class switchServlet extends HttpServlet {
	private static final Path SHADOWSOCKS_CONFIG = Paths.get("/etc/shadowsocks-libev/config.json");
	private static String newPort;

	private static void updatePort() {
		newPort = String.valueOf(new Random().nextInt(9999));
	}

	private static void change() {
		try (Stream<String> lines = Files.lines(SHADOWSOCKS_CONFIG)) {
			List<String> replaced = lines.map(line -> line.replaceFirst("(?<=\"1)\\d{4}(?=\")", newPort))
					.collect(Collectors.toList());
			Files.write(SHADOWSOCKS_CONFIG, replaced);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void restartService() throws IOException {
		Runtime.getRuntime().exec("sudo systemctl restart shadowsocks-libev.service");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
		response.getWriter().print(newPort);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		updatePort();
		change();
		restartService();
	}

}
