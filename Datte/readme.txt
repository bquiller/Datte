Rajouter le champs CAND_KEY à RDV_CANDIDAT ; le renseigner :

update garnuche.rdv_candidat rdv set cand_key = (select cand_key 
	FROM GARNUCHE.PRE_ETUDIANT pre, garnuche.pre_candidat prec 
	WHERE rdv.cod_ind = pre.no_individu and prec.etud_numero = pre.etud_numero);
	
	
Mettre la colonne cand_key de rdv_candidat en not null et le cod_ind à nullable (changer la clé primaire)