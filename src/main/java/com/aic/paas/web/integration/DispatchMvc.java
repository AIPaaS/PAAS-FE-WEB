package com.aic.paas.web.integration;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aic.paas.frame.cross.bean.ModuInfo;
import com.aic.paas.frame.cross.bean.SysModu;
import com.aic.paas.frame.util.RequestKey;
import com.aic.paas.frame.util.SysFrameUtil;
import com.binary.core.util.BinaryUtils;
import com.binary.framework.exception.ServiceException;
import com.binary.framework.util.ControllerUtils;


@Controller
@RequestMapping("/dispatch")
public class DispatchMvc {
	
	
private final Pattern NUM_REGX = Pattern.compile("[0-9]+");
	
	
	@Value("${integration.wsys.root}")
	String wsysRoot;
	
		
	@Value("${integration.wdev.root}")
	String wdevRoot;
	
	
	@Value("${integration.wdep.root}")
	String wdepRoot;
	
	
	
	
	@RequestMapping("/mi/**")
	public String openModuById(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURI();
		int idx = url.indexOf("/mi/");
		if(idx < 0) throw new ServiceException(" is wrong url '"+url+"'! ");
		String id = url.substring(idx+4).trim();
		
		if(!NUM_REGX.matcher(id).matches()) {
			throw new ServiceException(" is wrong url '"+url+"'! ");
		}
		
		SysModu modu = SysFrameUtil.getModuById(Long.valueOf(id));
		if(modu == null) {
			throw new ServiceException(" not found modu by id '"+id+"'! ");
		}
		
		String mp = modu.getModuParam();
		if(!BinaryUtils.isEmpty(mp)) {
			return forwardParam(request, response, modu, mp);
		}else {
			return "forward:/sys/frame/cross/modu/openModuleById?moduleId="+id;
		}
	}
	
	
	
	
	@RequestMapping("/mc/**")
	public String openModuByCode(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURI();
		int idx = url.indexOf("/mc/");
		if(idx < 0) throw new ServiceException(" is wrong url '"+url+"'! ");
		String code = url.substring(idx+4).trim();
		
		SysModu modu = SysFrameUtil.getModuByCode(code);
		if(modu == null) {
			throw new ServiceException(" not found modu by code '"+code+"'! ");
		}
		
		String mp = modu.getModuParam();
		if(!BinaryUtils.isEmpty(mp)) {
			return forwardParam(request, response, modu, mp);
		}
		
		return "forward:/sys/frame/cross/modu/openModuleByCode?moduleCode="+code;
	}
	
	
	
	private String forwardParam(HttpServletRequest request, HttpServletResponse response, SysModu modu, String param) {
		ModuInfo info = new ModuInfo();
		info.setModu(modu);
		request.setAttribute(RequestKey.DYNAMIC_MODUINFO_KEY, info);
		return "forward:"+param;
	}
	
	
	
	
	@RequestMapping("/integration/wsys")
	public String forward2Wsys(HttpServletRequest request, HttpServletResponse response, String fwr) {
		BinaryUtils.checkEmpty(fwr, "fwr");
		fwr = ControllerUtils.formatContextPath(fwr);
		String url = this.wsysRoot + fwr;
		request.setAttribute("integration_url", url);
		return "forward:/layout/jsp/integration.jsp";
	}
	
	
		
	
	@RequestMapping("/integration/wdev")
	public String forward2Wdev(HttpServletRequest request, HttpServletResponse response, String fwr) {
		BinaryUtils.checkEmpty(fwr, "fwr");
		fwr = ControllerUtils.formatContextPath(fwr);
		String url = this.wdevRoot + fwr;
		request.setAttribute("integration_url", url);
		return "forward:/layout/jsp/integration.jsp";
	}
	
	
	
	
	@RequestMapping("/integration/wdep")
	public String forward2Wdep(HttpServletRequest request, HttpServletResponse response, String fwr) {
		BinaryUtils.checkEmpty(fwr, "fwr");
		fwr = ControllerUtils.formatContextPath(fwr);
		String url = this.wdepRoot + fwr;
		request.setAttribute("integration_url", url);
		return "forward:/layout/jsp/integration.jsp";
	}
	

}
