package com.web.curation.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.web.curation.dto.BroadCastingDto;
import com.web.curation.dto.UserDto;
import com.web.curation.service.BoardService;
import com.web.curation.service.TimeService;

@Service
public class KakaoAPI {

	@Value("${KAKAO_API_KEY}")
	private String API_KEY;
	@Autowired
	TimeService timeService;
	@Autowired
	private BoardService boardService;

	public String getAccessToken(String authorize_code) {
		String access_Token = "";
		String refresh_Token = "";
		String reqURL = "https://kauth.kakao.com/oauth/token";
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// POST 요청을 위해 기본값이 false인 setDoOutput을 true로
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			// POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			sb.append("&client_id=" + API_KEY);
			sb.append("&redirect_uri=http://localhost:8080/social/login/kakao");
			sb.append("&code=" + authorize_code);
			bw.write(sb.toString());
			bw.flush();

			// 결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();

			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}

			// Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			access_Token = element.getAsJsonObject().get("access_token").getAsString();
			refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return access_Token;
	}

	public UserDto getUserInfo(String access_Token) {
		UserDto userDto = new UserDto();
		String reqURL = "https://kapi.kakao.com/v2/user/me";

		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			conn.setRequestProperty("Authorization", "Bearer " + access_Token);

			int responseCode = conn.getResponseCode();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();

			if (properties.getAsJsonObject().get("nickname") != null)
				userDto.setU_name(properties.getAsJsonObject().get("nickname").getAsString());
			userDto.setU_email(element.getAsJsonObject().get("id").getAsString());
			if (properties.getAsJsonObject().get("thumbnail_image") != null) {
				userDto.setU_thumbnail(properties.getAsJsonObject().get("thumbnail_image").getAsString());
			} else {
				userDto.setU_thumbnail("");
			}
			if (properties.getAsJsonObject().get("profile_image") != null) {
				userDto.setU_profileImg(properties.getAsJsonObject().get("profile_image").getAsString());
			} else {
				userDto.setU_profileImg("");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return userDto;
	}

	// 나에게 메세지 보내기
	public void messageForMe(Map<String, Object> map) {
		String reqURL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

		String accessToken = (String) map.get("accessToken");
		BroadCastingDto bcDto = (BroadCastingDto) map.get("broadCastingDto");
		String bc_img = timeService.GetImgByIdx(bcDto.getA_idx());
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			String str = "";
			if(bcDto.getBc_company()!= null) {
				str = bcDto.getBc_company();
			} else {
				str = boardService.singerSearch(bcDto.getS_idx()).getS_name();
			}
			String msg = "template_object={\r\n" + "        \"object_type\": \"feed\",\r\n"
					+ "        \"content\": {\r\n" + "            \"title\": \"" + bcDto.getBc_title() + "\",\r\n"
					+ "            \"description\": \"" + str + ", " + bcDto.getBc_time() + "\",\r\n"
					+ "            \"image_url\": \"" + bc_img + "\",\r\n" + "            \"image_width\": 640,\r\n"
					+ "            \"image_height\": 640,\r\n" + "            \"link\": {\r\n"
					+ "            \"web_url\": \"http://i3b202.p.ssafy.io\",\r\n"
					+ "            \"mobile_web_url\": \"http://i3b202.p.ssafy.io\",\r\n"
					+ "            \"android_execution_params\": \"contentId=100\",\r\n"
					+ "            \"ios_execution_params\": \"contentId=100\"\r\n" + "            }\r\n"
					+ "        }\r\n" + "    }";

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(msg);
			wr.flush();

			int responseCode = conn.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 로그아웃
	public void kakaoLogout(String access_Token) {
		String reqURL = "https://kapi.kakao.com/v1/user/logout";

		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			// 요청에 필요한 Header에 포함될 내용
			conn.setRequestProperty("Authorization", "Bearer " + access_Token);

			int responseCode = conn.getResponseCode();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 카카오 계정 로그아웃
	public void kakaoUnlink(String access_Token) {

		String reqURL = "https://kapi.kakao.com/v1/user/unlink";
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			// 요청에 필요한 Header에 포함될 내용
			conn.setRequestProperty("Authorization", "Bearer " + access_Token);
			int responseCode = conn.getResponseCode();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
