package org.cocktail.datte;

//
// DBHandler.java
// EdtScol
//
// Created by Adour on Tue Apr 13 2004.
// Copyright (c) 2004 __Université de La Rochelle__. All rights reserved.
//

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

public class DBHandler {

	/** fait une selection de la derniere valeur d'une sequence */
	public static Number seqValue(String seqName, EOEditingContext eContext) {
		EOFetchSpecification myFetch = new EOFetchSpecification(seqName, null, null);
		myFetch.setUsesDistinct(true);
		NSArray retour = eContext.objectsWithFetchSpecification(myFetch);
		if (retour.count() > 0) {
			return (Number) ((EOGenericRecord) retour.objectAtIndex(0)).valueForKey("nextval");
		}
		else {
			return null;
		}
	}

	/** renvoi un qualifier construit avec l'expression et les valeurs */
	public static EOQualifier getQualifier(String expression, NSArray values) {
		return EOQualifier.qualifierWithQualifierFormat(expression, values);
	}

	/** renvoi un qualifier construit avec l'expression */
	public static EOQualifier getQualifier(String expression) {
		return EOQualifier.qualifierWithQualifierFormat(expression, null);
	}

	/**
	 * renvoi un qualifier construit avec le champs field et la valeur que l'on veut lui associer. exemple :
	 * DBHandler.getSimpleQualifier("individuUlr",eoIndividu)
	 */
	public static EOQualifier getSimpleQualifier(String field, Object value) {
		return EOQualifier.qualifierWithQualifierFormat(field + "=%@", new NSArray(value));
	}

	/**
	 * EOEditingContext context String tableName String key Object value
	 */
	public static EOGenericRecord fetchUniqueData(EOEditingContext context, String tableName, String key, Object value) {
		EOQualifier qualifie = EOQualifier.qualifierWithQualifierFormat(key + "=%@", new NSArray(value));
		NSArray objets = fetchData(context, tableName, qualifie);
		if (objets.count() > 0) {
			return (EOGenericRecord) objets.objectAtIndex(0);
		}
		else {
			return null;
		}
	}

	/**
	 * EOEditingContext eContext, String tableName, EOQualifier leQualifier
	 */
	public static EOGenericRecord fetchUniqueData(EOEditingContext eContext, String tableName, EOQualifier qualifier) {
		NSArray objets = fetchData(eContext, tableName, qualifier);
		if (objets.count() > 0) {
			return (EOGenericRecord) objets.objectAtIndex(0);
		}
		else {
			return null;
		}
	}

	/** fetch avec une chaine et une valeur correspondante */
	public static NSArray fetchData(EOEditingContext context, String tableName, String key, Object value) {
		EOQualifier qualifie = EOQualifier.qualifierWithQualifierFormat(key + "=%@", new NSArray(value));
		return fetchData(context, tableName, qualifie);
	}

	public static NSArray fetchData(EOEditingContext context, String leNomTable, EOQualifier leQualifier) {
		EOFetchSpecification myFetch = new EOFetchSpecification(leNomTable, leQualifier, null);
		myFetch.setUsesDistinct(true);
		return context.objectsWithFetchSpecification(myFetch);
	}

	/** retourne l'objet du globalID s'il existe, sinon son fault */
	public static EOGenericRecord safeObjectForGlobalID(EOEditingContext eContext, EOGlobalID gid) {
		EOGenericRecord objFault;
		objFault = (EOGenericRecord) eContext.objectForGlobalID(gid);
		if (objFault != null) {
			return objFault;
		}
		else {
			return (EOGenericRecord) eContext.faultForGlobalID(gid, eContext);
		}
	}

	/** retourne les globalIDs des objets */
	public static NSArray globalIDsForObjects(EOEditingContext eContext, NSArray objects) {
		NSMutableArray gids = new NSMutableArray();
		for (int i = 0; i < objects.count(); i++) {
			gids.addObject(eContext.globalIDForObject((EOEnterpriseObject) objects.objectAtIndex(i)));
		}
		return gids;
	}

	/** retourne des faults d'objets à partir des globalIDs */
	public static NSArray faultsForGlobalIDs(EOEditingContext eContext, NSArray ids) {
		NSMutableArray objects = new NSMutableArray();
		for (int i = 0; i < ids.count(); i++) {
			objects.addObject(eContext.faultForGlobalID((EOGlobalID) ids.objectAtIndex(i), eContext));
		}
		return objects;
	}

	/** retourne les globalIDs des objets */
	public static NSArray objectsForGlobalIDs(EOEditingContext eContext, NSArray ids) {
		NSMutableArray objects = new NSMutableArray();
		EOGenericRecord record = null;
		for (int i = 0; i < ids.count(); i++) {
			record = (EOGenericRecord) eContext.objectForGlobalID((EOGlobalID) ids.objectAtIndex(i));
			if(record!=null) {
				objects.addObject(record);
			}
		}
		return objects;
	}

	/** obtenir la valeur de la cle primaire a partir d'un Object quand celle-ci est cachee */
	public static Object primaryKeyForObject(EOEditingContext eContext, EOEnterpriseObject record, String primaryKeyName) {
		try {
			return EOUtilities.primaryKeyForObject(eContext, record).objectForKey(primaryKeyName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** cree une instance de l'enregistrement d'entite entity et avec l'editingContext eContext */
	public static EOGenericRecord getInstance(EOEditingContext eContext, String entity) {
		EOClassDescription descriptionClass = EOClassDescription.classDescriptionForEntityName(entity);
		EOGenericRecord instance = (EOGenericRecord) descriptionClass.createInstanceWithEditingContext(eContext, null);
		eContext.insertObject(instance);
		return instance;
	}

	public static Object primaryKey(EOEditingContext eContext, EOGlobalID gid, String primKey) {
		EOEnterpriseObject record = eContext.faultForGlobalID(gid, eContext);
		if (record != null) {
			return primaryKey(eContext, record, primKey);
		}
		else {
			return null;
		}
	}

	public static Object primaryKey(EOEditingContext eContext, EOEnterpriseObject record, String primKey) {
		try {
			if (record == null) {
				return null;
			}
			else {
				NSDictionary keys = EOUtilities.primaryKeyForObject(eContext, record);
				return keys.objectForKey(primKey);
			}
		} // try
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** enleve tous les autres pointeurs d'un objet */
	public static NSMutableArray retirerMultiples(NSArray array) {
		NSMutableArray resultat = new NSMutableArray();
		for (int i = 0; i < array.count(); i++) {
			Object obj = array.objectAtIndex(i);
			if (!resultat.containsObject(obj)) {
				resultat.addObject(obj);
			}
		}
		return resultat;
	}

	public static void invalidateObjects(EOEditingContext ec, NSArray list) {
		NSMutableArray listGIDs = new NSMutableArray();
		for (int i = 0; i < list.count(); i++) {
			listGIDs.addObject(ec.globalIDForObject((EOEnterpriseObject) list.objectAtIndex(i)));
		}
		ec.invalidateObjectsWithGlobalIDs(listGIDs);
	}

	/** invalide l'object record passe en parametre */
	public static void invalidateObject(EOEditingContext ec, EOGenericRecord record) {
		NSArray listGIDs = new NSArray(new Object[] { ec.globalIDForObject(record) });
		ec.invalidateObjectsWithGlobalIDs(listGIDs);
	}

}
