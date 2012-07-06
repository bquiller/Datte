/*
 * Copyright COCKTAIL (www.cocktail.org), 1995, 2011 This software
 * is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or
 * redistribute the software under the terms of the CeCILL license as
 * circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * As a counterpart to the access to the source code and rights to copy, modify
 * and redistribute granted by the license, users are provided only with a
 * limited warranty and the software's author, the holder of the economic
 * rights, and the successive licensors have only limited liability. In this
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally,
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge
 * of the CeCILL license and that you accept its terms.
 */

package org.cocktail.datte.utils.reports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Map;

/**
 * @author Rodolphe PRIN - rodolphe.prin at cocktail.org
 */
public abstract class ZAbstractReport {
	public static final int MODE_SQL = 1;
	public static final int MODE_XML = 2;

	public static String NOPAGES_MSG = "Aucune donnee a imprimer. Verifiez eventuellement les criteres.";

	protected ReportFactory myReportFactory;
	protected IZAbstractReportListener myListener;

	private int _mode;

	private Connection _connection;
	private String _query;
	private Map _parameters;
	private String _fileName;
	private StringWriter _xmlDataSourceWriter;
	private String _xmlRecordPath;

	/**
	 * @param jasperFileName
	 * @param connection
	 * @param query
	 * @param parameters
	 * @param listener
	 * @throws Exception
	 */
	protected ZAbstractReport(final String jasperFileName, final Connection connection, final String query, final Map parameters, IZAbstractReportListener listener) throws Exception {
		super();
		_mode = MODE_SQL;
		_connection = connection;
		_query = query;
		myListener = listener;
		_parameters = parameters;
		_fileName = jasperFileName;

		//        ZLogger.info("");
		//        ZLogger.info("Generation d'un report avec connection JDBC = " + jasperFileName);
		//        ZLogger.info("SQL = " + query);        

		myReportFactory = new ReportFactory(jasperFileName, myListener);

		if (Thread.currentThread().isInterrupted()) {
			return;
		}

		myReportFactory.initEmptyDataSource();
		//        if (!Thread.currentThread().isInterrupted()) {
		//            System.out.println("Impression en cours - source de donnees SQL : "+jasperFileName +"...");
		//            myReportFactory.printReport(connection, query, parameters);
		//        }

	}

	//    
	//    protected ZAbstractReport(final String jasperFileName, final StringWriter xmlDataSourceWriter, final String xmlRecordPath, final Map parameters, IZAbstractReportListener listener) throws Exception {
	//        super();
	//        _mode = MODE_XML;
	//        _xmlDataSourceWriter = xmlDataSourceWriter;
	//        _xmlRecordPath = xmlRecordPath;
	//        myListener = listener;
	//        _parameters = parameters;
	//        _fileName = jasperFileName;        
	//        myListener = listener;
	//        
	////        ZLogger.info("");
	////        ZLogger.info("Generation d'un report avec datasource XML = " + jasperFileName);
	//        
	//        myReportFactory = new ReportFactory(jasperFileName, myListener);
	//
	//        if (Thread.currentThread().isInterrupted()) {
	//          return;
	//        }
	//
	//        myReportFactory.initEmptyDataSource();
	//    }
	//    

	public void prepareDataSource() throws Exception {
		if (_mode == MODE_XML) {
			if (_xmlDataSourceWriter != null) {
				final byte[] xmlbytes = _xmlDataSourceWriter.toString().getBytes();
				final InputStream xmlStream = new ByteArrayInputStream(xmlbytes);
				myReportFactory.initDataSource(xmlStream, _xmlRecordPath);
			}
			else {
				myReportFactory.initEmptyDataSource();
			}

		}
	}

	public void printReport() throws Exception {
		if (!Thread.currentThread().isInterrupted()) {
			switch (_mode) {
			case MODE_SQL:
				//ZLogger.info("Impression en cours - source de donnees SQL : "+_fileName +"...");
				myReportFactory.printReport(_connection, _query, _parameters);
				break;

			case MODE_XML:
				//ZLogger.info("Impression en cours - source de donnees XML : "+_fileName+"...");
				myReportFactory.printReport(_parameters);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * @return Le PDF genere par Jasper sous forme de OutputStream.
	 * @throws Exception
	 */
	public ByteArrayOutputStream getPdfOutputStream() throws Exception {
		//	    System.out.println("ZAbstractReport.getPdfOutputStream()");
		return myReportFactory.exportToPdf();
	}

	public ByteArrayOutputStream getXlsOutputStream() throws Exception {
		//	    System.out.println("ZAbstractReport.getPdfOutputStream()");
		return myReportFactory.exportToXls();
	}

	//
	//	protected static StringWriter createXMLDataSourceFromSqlQuery(final EOEditingContext ec, final String modelName, final String sqlQuery, final String elementTag, final String rootTag) throws Exception {
	//		return createXMLDataSourceFromSqlQuery(ec, modelName, sqlQuery, elementTag, rootTag, false);
	//	}
	//
	//	protected static StringWriter createXMLDataSourceFromSqlQuery(final EOEditingContext ec, final String modelName, final String sqlQuery, final String elementTag, final String rootTag, final boolean flat) throws Exception {
	//		return createXMLDataSourceFromSqlQuery(ec, modelName, sqlQuery, elementTag, rootTag, flat, true, true);
	//	}
	//
	//	protected static StringWriter createXMLDataSourceFromSqlQuery(final EOEditingContext ec, final String modelName, final String sqlQuery, final String elementTag, final String rootTag, final boolean flat, final boolean compact, final boolean escapeCarriageReturn) throws Exception {
	//		StringWriter myStringWriter = new StringWriter();
	//		CktlXMLWriter myCktlXMLWriter = new CktlXMLWriter(myStringWriter);
	//		myCktlXMLWriter.setUseCompactMode(compact);
	//		myCktlXMLWriter.startDocument();
	//		myCktlXMLWriter.startElement(rootTag);
	//		int nbres = ZXMLFactory.addResultFromSQLQuery(myCktlXMLWriter, ec, modelName, sqlQuery, elementTag, flat, escapeCarriageReturn);
	//		myCktlXMLWriter.endElement();
	//		myCktlXMLWriter.close();
	//		if (nbres == 0) {
	//			throw new Exception(NOPAGES_MSG);
	//		}
	//		return myStringWriter;
	//	}
	//
	//	protected static StringWriter createXMLDataSourceEmpty(final EOEditingContext ec, final String rootTag) throws Exception {
	//		StringWriter myStringWriter = new StringWriter();
	//		CktlXMLWriter myCktlXMLWriter = new CktlXMLWriter(myStringWriter);
	//		myCktlXMLWriter.startDocument();
	//		myCktlXMLWriter.startElement(rootTag);
	//
	//		myCktlXMLWriter.endElement();
	//		myCktlXMLWriter.close();
	//		return myStringWriter;
	//	}

	/**
	 * Parametre le report pour gï¿½nï¿½rer ou non des pages suivants la cas : JasperReport.WHEN_NO_DATA_TYPE_NO_PAGES,
	 * JasperReport.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL, JasperReport.WHEN_NO_DATA_TYPE_BLANK_PAGE
	 * 
	 * @param whenNoData
	 */
	public void setWhenNoDataType(final byte whenNoData) {
		myReportFactory.setWhenNoDataType(whenNoData);
	}

	public interface IZAbstractReportListener extends ReportFactory.IReportFactorylistener {

	}

}
