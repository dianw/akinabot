package org.codenergic.akinabot.akinator.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class Elements {
	@JsonProperty
	private Element element;

	public Element getElement() {
		return element;
	}


	@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
	public static class Element {
		@JsonProperty
		private String id;
		@JsonProperty
		private String name;
		@JsonProperty("id_base")
		private String idBase;
		@JsonProperty
		private String proba;
		@JsonProperty
		private String description;
		@JsonProperty("valide_contrainte")
		private String valideContrainte;
		@JsonProperty
		private String ranking;
		@JsonProperty("minibase_addable")
		private String minibaseAddable;
		@JsonProperty("relative_id")
		private String relativeId;
		@JsonProperty
		private String pseudo;
		@JsonProperty("picture_path")
		private String picturePath;
		@JsonProperty("absolute_picture_path")
		private String absolutePicturePath;

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getIdBase() {
			return idBase;
		}

		public String getProba() {
			return proba;
		}

		public String getDescription() {
			return description;
		}

		public String getValideContrainte() {
			return valideContrainte;
		}

		public String getRanking() {
			return ranking;
		}

		public String getMinibaseAddable() {
			return minibaseAddable;
		}

		public String getRelativeId() {
			return relativeId;
		}

		public String getPseudo() {
			return pseudo;
		}

		public String getPicturePath() {
			return picturePath;
		}

		public String getAbsolutePicturePath() {
			return absolutePicturePath;
		}
	}
}
