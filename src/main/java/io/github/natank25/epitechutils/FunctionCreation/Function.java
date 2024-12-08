package io.github.natank25.epitechutils.FunctionCreation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Function {
	
	@NotNull
	public String functionName;
	
	@Nullable
	public String returnType = null;
	
	public final Map<String, String> parameters = new HashMap<>();
	
	public boolean isStatic = false;
	
	public Function(@NotNull String functionName) {
		this.functionName = functionName;
	}
	
	public String parametersToString(){
		if (parameters.isEmpty())
			return "void";
		return parameters.entrySet().stream()
				.map(entry -> entry.getKey() + " " + entry.getValue())
				.collect(Collectors.joining(", "));
	}
	
	public String returnTypeToString(){
		return this.returnType == null ? "void" : returnType.replace(" ", "");
	}
	
	public String isStaticToString(){
		return isStatic ? "static " : "";
	}
	
	@Override
	public String toString() {
		return isStaticToString() + returnTypeToString() + " " + this.functionName + "(" + parametersToString() + ")\n{\n}";
	}
}
