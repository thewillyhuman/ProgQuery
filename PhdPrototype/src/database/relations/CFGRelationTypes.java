package database.relations;

public enum CFGRelationTypes implements RelationTypesInterface {

	CFG_ENTRY, CFG_END_OF, CFG_NEXT_CONDITION, CFG_NEXT_STATEMENT(
			CFG_NEXT_CONDITION), CFG_NEXT_COND_IF_TRUE, CFG_NEXT_STATEMENT_IF_TRUE(
					CFG_NEXT_COND_IF_TRUE), CFG_NEXT_COND_IF_FALSE, CFG_NEXT_STATEMENT_IF_FALSE(
							CFG_NEXT_COND_IF_FALSE), CFG_HAS_NEXT_TO_CONDITION, CFG_NO_MORE_ELEMENTS_TO_CONDITION, CFG_FOR_EACH_HAS_NEXT(
									CFG_HAS_NEXT_TO_CONDITION), CFG_FOR_EACH_NO_MORE_ELEMENTS(
											CFG_NO_MORE_ELEMENTS_TO_CONDITION), UNCAUGHT_EXCEPTION_TO_COND, UNCAUGHT_EXCEPTION(
													UNCAUGHT_EXCEPTION_TO_COND), NO_EXCEPTION_TO_COND, NO_EXCEPTION(
															NO_EXCEPTION_TO_COND), CAUGHT_EXCEPTION_TO_COND, CAUGHT_EXCEPTION(
																	CAUGHT_EXCEPTION_TO_COND), AFTER_FINALLY_PREVIOUS_BREAK_TO_COND, AFTER_FINALLY_PREVIOUS_BREAK(
																			AFTER_FINALLY_PREVIOUS_BREAK_TO_COND), AFTER_FINALLY_PREVIOUS_CONTINUE_TO_COND, AFTER_FINALLY_PREVIOUS_CONTINUE(
																					AFTER_FINALLY_PREVIOUS_CONTINUE_TO_COND), SWITCH_CASE_IS_EQUAL_TO_COND, SWITCH_CASE_IS_EQUAL_TO(
																							SWITCH_CASE_IS_EQUAL_TO_COND), SWITCH_DEFAULT_CASE_TO_COND, SWITCH_DEFAULT_CASE(
																									SWITCH_DEFAULT_CASE_TO_COND), MAY_THROW_TO_COND, MAY_THROW(
																											MAY_THROW_TO_COND);
	private CFGRelationTypes toCondition;

	public static String getCFGRelations() {
		String ret = "";
		for (CFGRelationTypes cfgRel : CFGRelationTypes.values())
			ret += cfgRel.name() + " | ";
		return ret.substring(0, ret.length() - 3);
	}

	public CFGRelationTypes toCondition() {
		return toCondition;
	}

	private CFGRelationTypes() {
		toCondition = null;
	}

	private CFGRelationTypes(CFGRelationTypes r) {
		toCondition = r;
	}

}
