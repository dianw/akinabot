package akinabot.model.akinator;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListResponse {
	private String completion;
	private ListParameters parameters;

	public String getCompletion() {
		return completion;
	}

	public void setCompletion(String completion) {
		this.completion = completion;
	}

	public ListParameters getParameters() {
		return parameters;
	}

	public void setParameters(ListParameters parameters) {
		this.parameters = parameters;
	}

	public static class ListParameters {
		private List<Elements> elements = new ArrayList<>();
		private String nbObjetsPertinents;

		public List<Elements> getElements() {
			return elements;
		}

		public void setElements(List<Elements> elements) {
			this.elements = elements;
		}

		@JsonProperty("NbObjetsPertinents")
		public String getNbObjetsPertinents() {
			return nbObjetsPertinents;
		}

		public void setNbObjetsPertinents(String nbObjetsPertinents) {
			this.nbObjetsPertinents = nbObjetsPertinents;
		}
	}
}
