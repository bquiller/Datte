package org.cocktail.datte.utils;

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

import java.io.ByteArrayOutputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;

import org.cocktail.datte.Application;
import org.cocktail.datte.Session;
import org.cocktail.datte.utils.reports.DefaultReport;
import org.cocktail.datte.utils.reports.DefaultReport.IDefaultReportListener;
import org.cocktail.datte.utils.reports.ZAbstractReport;

import com.webobjects.foundation.NSData;

/**
 * Gestion des appels distants (clients) concernant les impressions.
 * 
 * @author Rodolphe PRIN <rodolphe.prin at cocktail.org>
 */

public class PrintDelegate {
	public final static int FORMATXLS = 0;
	public final static int FORMATPDF = 1;

	private ZAbstractReport lastReport;
	private ZAbstractReport currentReport;
	private Exception lastPrintError;

	private Session mySession;

	public PrintDelegate(Session session) {
		mySession = session;
	}

	public Session getMySession() {
		return mySession;
	}


	public class MyDefaultReportListener implements IDefaultReportListener {
		private int _pageNum = -1;
		private int _pageCount = -1;
		private int _pageTotalCount = -1;
		private boolean dataSourceCreated = false;
		private boolean reportBuild = false;
		private boolean reportExported = false;

		public synchronized int getPageCount() {
			return _pageCount;
		}

		public synchronized int getPageTotalCount() {
			return _pageTotalCount;
		}

		public synchronized int getPageNum() {
			return _pageNum;
		}

		public synchronized boolean isDataSourceCreated() {
			return dataSourceCreated;
		}

		public synchronized boolean isReportExported() {
			return reportExported;
		}

		public synchronized boolean isReportBuild() {
			return reportBuild;
		}

		/**
		 * @see org.cocktail.jefyadmin.server.reports.DefaultReport.IDefaultReportListener#afterDataSourceCreated()
		 */
		public synchronized void afterDataSourceCreated() {
			dataSourceCreated = true;
			Thread.yield();
		}

		/**
		 * @see org.cocktail.jefyadmin.server.reports.ReportFactory.IReportFactorylistener#afterReportBuild()
		 */
		public synchronized void afterReportBuild(int pageCount) {
			//            getSessLog().trace("");
			reportBuild = true;
			_pageTotalCount = pageCount;
			Thread.yield();
		}

		/**
		 * @see org.cocktail.jefyadmin.server.reports.ReportFactory.IReportFactorylistener#afterPageExport(int, int)
		 */
		public synchronized void afterPageExport(int pageNum, int pageCount) {
			//            getSessLog().trace("");
			_pageCount = pageCount;
			_pageNum = pageNum;
			Thread.yield();
		}

		/**
		 * @see org.cocktail.jefyadmin.server.reports.ReportFactory.IReportFactorylistener#afterReportExport()
		 */
		public synchronized void afterReportExport() {
			//            getSessLog().trace("");
			reportExported = true;
			Thread.yield();
		}

	}

	public NSData printNow(Application app, String sqlQuery, final String jasperFileName, final Map parameters, final int printFormat, final Boolean printIfEmpty) {
		//		Map _params;
		//		String _sqlQuery;
		//		String _jasperFileName;
		MyDefaultReportListener reportListener;
		NSData pdf;
		final int _printFormat;
		final byte _modeBlank;

		//		_params = parameters;
		//		_sqlQuery = sqlQuery;
		//		_jasperFileName = jasperFileName;
		_printFormat = printFormat;
		if (printIfEmpty.booleanValue()) {
			_modeBlank = JasperReport.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL;
		}
		else {
			_modeBlank = JasperReport.WHEN_NO_DATA_TYPE_NO_PAGES;
		}
		try {
			lastReport = null;
			pdf = null;
			reportListener = new MyDefaultReportListener();
			currentReport = DefaultReport.createDefaultReport(app.getJDBCConnection(), sqlQuery, jasperFileName, parameters, null);

			currentReport.setWhenNoDataType(_modeBlank);
			currentReport.prepareDataSource();
			currentReport.printReport();
			lastReport = currentReport;
			Thread.yield();
			ByteArrayOutputStream s;
			switch (_printFormat) {
			case FORMATPDF:
				s = lastReport.getPdfOutputStream();
				break;

			case FORMATXLS:
				s = lastReport.getXlsOutputStream();
				break;

			default:
				s = lastReport.getPdfOutputStream();
				break;
			}

			pdf = new NSData(s.toByteArray());

			return pdf;
		} catch (Exception e) {
			lastPrintError = e;
			e.printStackTrace();
			System.err.println("impression vide avec les parametres = " + parameters);
			return null;
		}
	}

}
