package com.cafes.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafes.constants.CafeConstants;
import com.cafes.dao.BillDao;
import com.cafes.jwt.JwtFilter;
import com.cafes.pojo.Bill;
import com.cafes.service.BillService;
import com.cafes.utils.CafeUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64.InputStream;
@Service
public class BillServiceImpl implements BillService{

	@Autowired
	BillDao billDao;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			System.out.println("Inside generate Report");
			String fileName;
			if(validateRequestMap(requestMap)) {
				if(requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate") ) {
					fileName= (String) requestMap.get("uuid");
				}else {
					fileName=CafeUtils.getUUID();
					requestMap.put("uuid",fileName);
					insertBill(requestMap);// Save in Database
				}
				//-------------------------
				String Data = "Name : " + requestMap.get("name")+"\n"+"Contact Number : "+ requestMap.get("contactNumber")+"\n"+"Email : "+requestMap.get("email")+"\n"+"Payment Method : "+requestMap.get("paymentMethod");
				
				Document document = new Document();
				PdfWriter.getInstance(document,new FileOutputStream(CafeConstants.LOCATION+"\\"+fileName+".pdf"));
				
				document.open();
				// Rectangle add
				setRectangleInPdf(document);
				
				//Heading add
				Paragraph chunk = new Paragraph("Cafe Management System",getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);
				
				// 34:66 end video
				Paragraph para = new Paragraph(Data+"\n\n", getFont("Data"));
				document.add(para);
				
				//add table
				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeader(table);
				
				//table data add
				JSONArray jsonArry = CafeUtils.getJsonArrayFromString((String)requestMap.get("productDetails"));
				
				for(int i=0;i<jsonArry.length();i++) {
					addRows(table,CafeUtils.getMapFromJson(jsonArry.getString(i)));
				}
				
				document.add(table);
				
				// add footer
				Paragraph footer = new Paragraph("Total : "+requestMap.get("totalAmount")+"\n"+"Thank you for visiting us.Please visit again!!",getFont("Data"));
				document.add(footer);
				document.close();
				 return  new ResponseEntity<String>("UUID : "+fileName,HttpStatus.OK);
				 
				 // we can send by mail also........in future
				
				
				
			}
			return CafeUtils.getResponseEntity("Required Data Not Found",HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// add table data
	private void addRows(PdfPTable table, Map<String, Object> data ){
		table.addCell((String)data.get("name"));
		table.addCell((String)data.get("category"));
		table.addCell((String)data.get("quantity"));
		table.addCell(Double.toString((Double)data.get("price")));
		table.addCell(Double.toString((Double)data.get("total")));
	}

	//table header add use stream api and lambda expression for simplification
	private void addTableHeader(PdfPTable table) {
		Stream.of("Name","Category","Quantity","Price","Sub Total")
			.forEach(columnTitle->{
				PdfPCell head = new PdfPCell();
				head.setBackgroundColor(BaseColor.LIGHT_GRAY);
				head.setBorderWidth(2);
				head.setPhrase(new Phrase(columnTitle));
				head.setBackgroundColor(BaseColor.PINK);
				head.setHorizontalAlignment(Element.ALIGN_CENTER);
				head.setVerticalAlignment(Element.ALIGN_CENTER);
				table.addCell(head);
			});
		
	}

	private Font getFont(String type) {
		
		switch(type) {
		case "Header":
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
			headerFont.setStyle(Font.BOLD);
			return headerFont;
		case "Data":
			Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
			dataFont.setStyle(Font.BOLD);
			return dataFont;
		default :
			return new Font();	
		
		}
		
	}

	// Rectangle ---------
	private void setRectangleInPdf(Document document) throws DocumentException {
		Rectangle rect = new Rectangle(577,825,18,15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		
		rect.setBorderColor(BaseColor.BLACK);
		rect.setBorderWidth(1);
		
		document.add(rect);
		
	}

	private void insertBill(Map<String, Object> requestMap) {
		try {
			
			Bill bill = new Bill();
			bill.setUuid((String) requestMap.get("uuid"));
			bill.setName((String) requestMap.get("name"));
			bill.setEmail((String) requestMap.get("email"));
			bill.setContactNumber((String) requestMap.get("contactNumber"));
			bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
			bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
			//System.out.println((String) requestMap.get("productDetails"));
			//System.out.println(Integer.parseInt((String) requestMap.get("totalAmount")));
			//System.out.println("error here");
			bill.setProductDetails((String) requestMap.get("productDetails"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());
			
			// save object
			billDao.save(bill);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private boolean validateRequestMap(Map<String, Object> requestMap) {
		
		return (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("paymentMethod") && requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount"));
	}

	@Override
	public ResponseEntity<?> getBills() {
		List<Bill> list = new ArrayList<>();
		if(jwtFilter.isAdmin()) {
			list= billDao.getAllBills();// all bills in decending order
		}else {
			list= billDao.getBillByUserName(jwtFilter.getCurrentUser());
		}
		return new ResponseEntity<>(list,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional optional= billDao.findById(id);
			if(!optional.isEmpty()) {
				billDao.deleteById(id);
				return  CafeUtils.getResponseEntity("Bill Deleted SuccessFully",HttpStatus.OK);
			}
			
			return CafeUtils.getResponseEntity("Bill ID Does Not Exists",HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<?> getPdf(Map<String, Object> requestMap) {
		System.out.println((String)requestMap.get("uuid"));
		System.out.println((String)requestMap.get("email"));
		try {
			byte[] byteArray= new byte[0];
			if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
				return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
			String filePath = "E:\\lernhtml\\Cafes\\"+(String)requestMap.get("uuid")+".pdf";
			System.out.println((String)requestMap.get("uuid"));
			if(CafeUtils.isFileExists(filePath)) {
				byteArray=getByteArray(filePath);
				return  new ResponseEntity<>(byteArray,HttpStatus.OK);
			}else {
				requestMap.put("isGenerate",false);
				generateReport(requestMap);
				byteArray=getByteArray(filePath);
				return  new ResponseEntity<>(byteArray,HttpStatus.OK);
			}
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private byte[] getByteArray(String filePath) throws IOException {
		File initialFile = new File(filePath);
	    FileInputStream inp = new FileInputStream(initialFile);
		byte[] byteArray = IOUtils.toByteArray(inp);
		inp.close();
		return byteArray;
	}
	
	

}
