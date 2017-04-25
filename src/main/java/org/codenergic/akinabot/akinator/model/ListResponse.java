package org.codenergic.akinabot.akinator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class ListResponse implements Completable {
	@JsonProperty
	private String completion;
	@JsonProperty
	private ListParameters parameters;

	@Override
	public String getCompletion() {
		return completion;
	}

	public ListParameters getParameters() {
		return parameters;
	}

	@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
	public static class ListParameters {
		@JsonProperty
		private List<Elements> elements = new ArrayList<>();
		@JsonProperty("NbObjetsPertinents")
		private String nbObjetsPertinents;

		public List<Elements> getElements() {
			return elements;
		}

		public String getNbObjetsPertinents() {
			return nbObjetsPertinents;
		}
	}
}
