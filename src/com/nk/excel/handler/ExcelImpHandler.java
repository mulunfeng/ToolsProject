package com.nk.excel.handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nk.excel.annotation.ExcelExpImp;
import com.nk.excel.entity.XCell;
import com.nk.excel.entity.XRow;
import com.nk.excel.util.DateUtils;
import com.nk.excel.util.DecimalUtils;
import com.nk.excel.util.StringUtil;

/**
 * @author young 通用EXCEL导入handler
 */
public class ExcelImpHandler extends AbstractDetectHandler implements
		IDetectHandler {

	private List detectDetails = new ArrayList();
	private List<String> errMsgList = new ArrayList<String>();
	private static String SET_STR = "set";
	private static String GET_STR = "get";

	/** 列数 */
	private static int columnSize;
	private int startRow;
	private Class<?> clazz;
	private Map<Integer, Method> methodMap;

	private static final ValidatorFactory validatorFactory = Validation
			.buildDefaultValidatorFactory();
	private static final Validator validator = validatorFactory.getValidator();

	private static final Logger logger = LoggerFactory
			.getLogger(ExcelImpHandler.class);

	public ExcelImpHandler(boolean needRowNum, String clazzName,
			File templateFile) throws ClassNotFoundException,
			InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(templateFile);
		Sheet sheet = workbook.getSheetAt(0);
		this.startRow = sheet.getLastRowNum();
		this.clazz = Class.forName(clazzName);

		Method[] methods = clazz.getMethods();
		Map<Integer, Method> map = new HashMap<Integer, Method>();
		for (int j = 0; j < methods.length; j++) {
			if (methods[j].isAnnotationPresent(ExcelExpImp.class)) {
				map.put(methods[j].getAnnotation(ExcelExpImp.class).value(),
						methods[j]);
			}
		}
		// sort by key
		methodMap = new TreeMap<Integer, Method>(map);
		columnSize = needRowNum ? methodMap.size() + 1 : methodMap.size();
	}

	@Override
	public void readRows(XRow row) {
		// 开始读取行数：0
		if (row.getRowIndex() < startRow + 1) {
			return;
		}
		List<String> rowStrs = new LinkedList<String>();
		for (XCell cell : row.getCells()) {
			rowStrs.add(cell.getValue());
		}
		int rowNum = row.getRowIndex() + 1;

		// 转换前校验
		boolean flag = validateExcelData(rowStrs, rowNum, errMsgList);
		if (flag) {
			Object object = null;
			try {
				object = voToPo(rowStrs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (object != null) {
				detectDetails.add(object);
			}
		}
	}

	@Override
	public boolean validateExcelData(List<String> list, int rowIndex,
			List<String> errList) {
		logger.info("所得列数：" + list.size());
		try {
			if (list.size() == columnSize) {
				Set<Entry<Integer, Method>> entrySet = methodMap.entrySet();
				for (Entry<Integer, Method> entry : entrySet) {
					String data = list.get(entry.getKey());
					Class<?> typeClass = entry.getValue().getReturnType();
					if (typeClass.equals(Double.class)
							|| typeClass.equals(Integer.class)) {
						if (!DecimalUtils.isNumber(data)) {
							errMsgList.add(" 第" + rowIndex + "行： "
									+ (entry.getKey() + 1) + "列必须为数值<br>");
						}
					} else if (typeClass.equals(Date.class)) {
						if ((!DateUtils.isDate(data))
								&& (!DateUtils.isDateTime(data))) {
							errMsgList.add(" 第" + rowIndex + "行： "
									+ (entry.getKey() + 1) + "列必须为日期类型<br>");
						}
					}
				}
			} else {
				errMsgList.add(" 第" + rowIndex + "行： "
						+ "列信息不完整或者您多填写了某些列，请重新填写后再导入!<br>");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return errMsgList.isEmpty();
	}

	private Object voToPo(List<String> list) throws InstantiationException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		Object obj = clazz.newInstance();
		Set<Integer> keys = methodMap.keySet();
		for (Integer key : keys) {
			Method setMethod = obj.getClass().getMethod(
					SET_STR + methodMap.get(key).getName().substring(3),
					methodMap.get(key).getReturnType());
			if (methodMap.get(key).getReturnType().equals(String.class)) {
				setMethod.invoke(obj, list.get(key));
			} else if (methodMap.get(key).getReturnType().equals(Integer.class)) {
				setMethod.invoke(obj, Integer.parseInt(list.get(key)));
			} else if (methodMap.get(key).getReturnType().equals(Date.class)) {
				setMethod.invoke(obj, DateUtils.parse(list.get(key)));
			}
		}
		return obj;
	}

	private Object getKeyByValue(Map<Integer, Method> map, Object value) {
		Set<Entry<Integer, Method>> set = map.entrySet();
		for (Entry<Integer, Method> entry : set) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	private boolean validateExcelData(List<?> detectDetails)
			throws NoSuchMethodException, SecurityException {
		boolean sig = true;
		int i = 2;
		for (Object entity : detectDetails) {
			Set<?> constraintViolation = validator.validate(entity,
					Default.class);
			Iterator<?> iterator = constraintViolation.iterator();
			if (iterator.hasNext()) {
				sig = false;
			}
			while (iterator.hasNext()) {
				@SuppressWarnings("unchecked")
				ConstraintViolation<?> obj = (ConstraintViolation<?>) iterator.next();
				StringBuffer methodBuffer = new StringBuffer();
				methodBuffer.append(GET_STR);
				methodBuffer.append(StringUtil.capitalize(obj.getPropertyPath()
						.toString()));
				Method method = entity.getClass().getMethod(methodBuffer.toString());
				Integer key = (Integer) getKeyByValue(methodMap, method);
				errMsgList.add("第" + (startRow + i++) + "行,第" + (key + 1) + "列"
						+ obj.getPropertyPath() + obj.getMessage() + "<br/>");
			}
		}
		return sig;
	}

	@Override
	public List<String> getErrorMessages() {
		try {
			validateExcelData(this.detectDetails);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return errMsgList;
	}

	@Override
	public void exportExcel(List<?> detectEntities,
			File templateFile, File targetFile) throws InvalidFormatException,
			IOException {
	}

	@Override
	public List<?> getDetectEntityList() {
		return this.detectDetails;
	}

}
