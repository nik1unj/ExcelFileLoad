package com.example.demo.service;


import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.User;
import com.example.demo.repository.FileReadRepository;


@Service
@Transactional
public class ServiceImple implements ReadFileService
{
   @Autowired private FileReadRepository fileReadRepository;

@Override
public List<User> findAll() {
	// TODO Auto-generated method stub
	return (List<User>) fileReadRepository.findAll();
}

@Override
public boolean saveDataFromUploadfile(MultipartFile file) {
	// TODO Auto-generated method stub
	boolean isFlag =false;
	String extension = FilenameUtils.getExtension(file.getOriginalFilename());
	if(extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx"))
	{
		isFlag = readDataFromExcel(file);
	}
	
	return isFlag;
}

private boolean readDataFromExcel(MultipartFile file) {
	Workbook workbook = getWorkBook(file);
	
	Sheet sheet = workbook.getSheetAt(0);
	Iterator<Row> rows = sheet.iterator();
	rows.next();
	while(rows.hasNext())
	{
	  Row row = rows.next();	
	 User user = new User();
	 if(row.getCell(0).getCellType()==Cell.CELL_TYPE_STRING)
	 {
		 user.setFirstName(row.getCell(0).getStringCellValue());
		 
	 }
	 if(row.getCell(1).getCellType()==Cell.CELL_TYPE_STRING)
	 {
		 user.setLastName(row.getCell(1).getStringCellValue());
	 }
	 if(row.getCell(2).getCellType()==Cell.CELL_TYPE_STRING)
	 {
		 user.setEmail(row.getCell(2).getStringCellValue());
	 }
	 if(row.getCell(3).getCellType()==Cell.CELL_TYPE_NUMERIC)
	 {
		 String s = NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
		 user.setPhoneNumber(s);
	 }
	 else if(row.getCell(3).getCellType()==Cell.CELL_TYPE_NUMERIC)
	 {
		
		 user.setPhoneNumber(row.getCell(3).getStringCellValue());
	 }
	 user.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
	 fileReadRepository.save(user);
	}
	
	
	
	return true;
}

private Workbook getWorkBook(MultipartFile file) {
	
	Workbook workbook= null;
	String extension = FilenameUtils.getExtension(file.getOriginalFilename());
	try {
		if(extension.equalsIgnoreCase("xlsx"))
		{
			workbook = new XSSFWorkbook(file.getInputStream());
		}
		else if(extension.equalsIgnoreCase("xls"))
		{
			workbook = new HSSFWorkbook(file.getInputStream());
		}
	}
	catch(Exception e) {
		e.printStackTrace();
	}

	return workbook;
}




   
      
   
}
