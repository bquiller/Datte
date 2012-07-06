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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Ctrl pour JaspertReports
 * 
 * @author Rodolphe PRIN - rodolphe.prin at cocktail.org
 */
public class ReportFactory {
	/** Nom de la cle dans le report qui represente la requete sql */
	public static final String SQLQUERY_KEY = "REQUETE_SQL";
	private JasperReport jasperReport;
	private JRDataSource _jrxmlds;
	private JasperPrint printResult;
	private Integer stateLock = new Integer(0); // Objet qui sert ï¿½ synchroniser les threads

	private int pageCount = -1;
	private int currentPageNum = -1;

	private IReportFactorylistener myListener;

	/**
     *
     */
	/**
	 * @param jasperFileName le nom du fichier .jasper
	 */
	public ReportFactory(String jasperFileName, IReportFactorylistener listener) {
		super();
		myListener = listener;
		try {
			pageCount = -1;
			currentPageNum = -1;

			final File f = new File(jasperFileName);
			if (!f.exists()) {
				throw new Exception("Le fichier " + jasperFileName + " n'existe pas.");
			}

			System.out.println("Chargement de " + jasperFileName + " ...");
			jasperReport = (JasperReport) JRLoader.loadObject(jasperFileName);
			//            jasperReport.setWhenNoDataType(JasperReport.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL);
			jasperReport.setWhenNoDataType(JasperReport.WHEN_NO_DATA_TYPE_NO_PAGES);

			System.out.println(jasperFileName + " charge.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parametre le report pour gï¿½nï¿½rer ou non des pages suivants la cas : JasperReport.WHEN_NO_DATA_TYPE_NO_PAGES,
	 * JasperReport.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL, JasperReport.WHEN_NO_DATA_TYPE_BLANK_PAGE
	 * 
	 * @param whenNoData
	 */
	public void setWhenNoDataType(final byte whenNoData) {
		jasperReport.setWhenNoDataType(whenNoData);
	}

	/**
	 * Initialise la datasource ï¿½ partir du flux xml
	 * 
	 * @param xmlDataStream Flux des datas xml
	 * @param xmlRecordPath Le recordPath
	 * @throws JRException
	 */
	public void initDataSource(InputStream xmlDataStream, String xmlRecordPath) throws JRException {
		_jrxmlds = new JRXmlDataSource(xmlDataStream, xmlRecordPath);
		System.out.println("ReportFactory.initDataSource() " + "Datasource XML creee avec recordpath : " + xmlRecordPath);
	}

	/**
	 * Initialise une datasource vide.
	 * 
	 * @throws JRException
	 */
	public void initEmptyDataSource() throws JRException {
		_jrxmlds = new JREmptyDataSource();
		System.out.println("ReportFactory.initDataSource() " + "Datasource vide cree");
	}

	/**
	 * @param parameters Parametres ï¿½ passer au report
	 * @throws JRException
	 */
	private void _printReport(final Map parameters) throws Exception {
		printResult = JasperFillManager.fillReport(jasperReport, parameters, _jrxmlds);
		pageCount = printResult.getPages().size();

		if (myListener != null) {
			myListener.afterReportBuild(pageCount);
		}
		Thread.yield();
		System.out.println("ReportFactory.printReport() " + " report rempli - " + pageCount + " pages");

		if (pageCount == 0) {
			throw new Exception(ZAbstractReport.NOPAGES_MSG);
		}

	}

	/**
	 * Remplit le report ï¿½ partir d'une requete sql spï¿½cifie en parametre. Fonctionne ï¿½galement si la requete est incluse dans le report (dans
	 * ce cas, spï¿½cifier null pour le parametre sqlQuery).
	 * 
	 * @param connection Connection ï¿½ la base de donnï¿½es.
	 * @param sqlQuery Requete SQL. Si la chaine n'est pas nulle, elle est ajoutï¿½e aux parametres passï¿½s au report (@see SQLQUERY_KEY)
	 * @param parameters Parametres ï¿½ passer au report
	 * @throws JRException
	 */
	private void _printReport(final Connection connection, final String sqlQuery, final Map parameters) throws Exception {
		//On ajoute la requete SQL aux parametres si celle-ci n'est pas nulle
		if (sqlQuery != null) {
			parameters.put(SQLQUERY_KEY, sqlQuery);
		}

		//        printResult = JasperFillManager.fillReport(jasperReport, null, connection);
		//        ZLogger.verbose(Locale.getDefault());

		printResult = JasperFillManager.fillReport(jasperReport, parameters, connection);

		pageCount = printResult.getPages().size();

		if (myListener != null) {
			myListener.afterReportBuild(pageCount);
		}
		Thread.yield();
		System.out.println("ReportFactory.printReport() " + " report rempli - " + pageCount + " pages");

		if (pageCount == 0) {
			if (jasperReport.getQuery() != null) {
				System.out.println("query recuperee dans le report avant exec = " + jasperReport.getQuery().getText());
			}
			if (jasperReport.getMainDataset().getQuery() != null) {
				System.out.println("query recuperee dans le report main dataset avant exec = " + jasperReport.getMainDataset().getQuery().getText());
			}

			throw new Exception(ZAbstractReport.NOPAGES_MSG);
		}

	}

	private Integer getStateLock() {
		return stateLock;
	}

	public void printReport(final Map parameters) throws Exception {
		_printReport(parameters);
	}

	public void printReport(final Connection connection, final String sqlQuery, final Map parameters) throws Exception {
		_printReport(connection, sqlQuery, parameters);
	}

	public ByteArrayOutputStream exportToPdf() throws JRException {
		final ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
		final JRExporter exporter = new JRPdfExporter();
		final JRExportProgressMonitor exportProgressMonitor = new JRExportProgressMonitor() {
			public void afterPageExport() {
				currentPageNum++;
				if (myListener != null) {
					myListener.afterPageExport(currentPageNum, pageCount);
					Thread.yield();
				}
			}
		};
		exporter.setParameter(JRExporterParameter.PROGRESS_MONITOR, exportProgressMonitor);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, tmpStream);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, printResult);
		currentPageNum = 1;
		exporter.exportReport();
		if (myListener != null) {
			myListener.afterReportExport();
			Thread.yield();
		}
		return tmpStream;
	}

	public ByteArrayOutputStream exportToXls() throws JRException {
		final ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
		final JRExporter exporter = new JRXlsExporter();
		final JRExportProgressMonitor exportProgressMonitor = new JRExportProgressMonitor() {
			public void afterPageExport() {
				currentPageNum++;
				if (myListener != null) {
					myListener.afterPageExport(currentPageNum, pageCount);
					Thread.yield();
				}
			}
		};

		exporter.setParameter(JRExporterParameter.PROGRESS_MONITOR, exportProgressMonitor);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, tmpStream);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, printResult);
		exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);

		currentPageNum = 1;
		exporter.exportReport();
		if (myListener != null) {
			myListener.afterReportExport();
			Thread.yield();
		}
		return tmpStream;
	}

	public interface IReportFactorylistener {
		public void afterReportBuild(int pageTotalCount);

		public void afterPageExport(int pageNum, int pageCount);

		public void afterReportExport();

	}

}
