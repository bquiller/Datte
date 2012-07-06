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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Map;

/**
 * Implementation par defaut de ZAbstractReport.
 * 
 * @author Rodolphe PRIN - rodolphe.prin at cocktail.org
 */
public class DefaultReport extends ZAbstractReport {
	//
	//    /**
	//     * Cree une instance de DefaultReport avec connection JDBC avec source de donnees XML
	//     * @param jasperFileName
	//     * @param xmlDataSourceWriter
	//     * @param xmlRecordPath
	//     * @param parameters
	//     * @throws Exception
	//     */
	//    public DefaultReport(String jasperFileName, StringWriter xmlDataSourceWriter, String xmlRecordPath, Map parameters, IDefaultReportListener listener) throws Exception {
	//        super(jasperFileName, xmlDataSourceWriter, xmlRecordPath, parameters, listener);
	//    }

	/**
	 * Cree une instance de DefaultReport avec connection JDBC vers la base de donnees.
	 * 
	 * @param jasperFileName
	 * @param connection
	 * @param query
	 * @param parameters
	 * @param listener
	 * @throws Exception
	 */
	public DefaultReport(final String jasperFileName, final Connection connection, final String query, final Map parameters, IZAbstractReportListener listener) throws Exception {
		super(jasperFileName, connection, query, parameters, listener);
	}

	public static final DefaultReport createDefaultReport(Connection connection, String sqlQuery, final String jasperFileName, final Map parameters, IDefaultReportListener listener) throws Exception {

		return new DefaultReport(jasperFileName, connection, sqlQuery, parameters, listener);
	}

	public static final void saveInTempFile(StringWriter sw, final String fileName) throws Exception {
		//        return;

		String temporaryDir = System.getProperty("java.io.tmpdir");
		if (!temporaryDir.endsWith(File.separator)) {
			temporaryDir = temporaryDir + File.separator;
		}
		File f = new File(temporaryDir + fileName);
		if (f.exists()) {
			f.delete();
		}
		if (f.createNewFile()) {
			new FileWriter(f);
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(sw.toString());
			out.close();
		}
		System.out.println("Le fichier " + f.getAbsolutePath() + " a ete cree.");
	}

	public interface IDefaultReportListener extends IZAbstractReportListener {
		void afterDataSourceCreated();
	}

}
