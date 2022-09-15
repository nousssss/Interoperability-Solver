/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2006 TU/e Eindhoven * and is licensed under the * Common
 * Public License, Version 1.0 * by Eindhoven University of Technology *
 * Department of Information Systems * http://is.tm.tue.nl * *
 **********************************************************/

/* Generated By:JavaCC: Do not edit this line. TpnParserTokenManager.java */
package org.processmining.plugins.petrinet.importing.tpn;

public class TpnParserTokenManager implements TpnParserConstants {
	public java.io.PrintStream debugStream = System.out;

	public void setDebugStream(java.io.PrintStream ds) {
		debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0) {
		switch (pos) {
			case 0 :
				if ((active0 & 0x7eL) != 0L) {
					jjmatchedKind = 9;
					return 1;
				}
				return -1;
			case 1 :
				if ((active0 & 0x38L) != 0L) {
					return 1;
				}
				if ((active0 & 0x46L) != 0L) {
					if (jjmatchedPos != 1) {
						jjmatchedKind = 9;
						jjmatchedPos = 1;
					}
					return 1;
				}
				return -1;
			case 2 :
				if ((active0 & 0x50L) != 0L) {
					return 1;
				}
				if ((active0 & 0xeL) != 0L) {
					jjmatchedKind = 9;
					jjmatchedPos = 2;
					return 1;
				}
				return -1;
			case 3 :
				if ((active0 & 0x6L) != 0L) {
					jjmatchedKind = 9;
					jjmatchedPos = 3;
					return 1;
				}
				if ((active0 & 0x8L) != 0L) {
					return 1;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_0(int pos, long active0) {
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
	}

	private final int jjStopAtPos(int pos, int kind) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private final int jjStartNfaWithStates_0(int pos, int kind, int state) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return pos + 1;
		}
		return jjMoveNfa_0(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_0() {
		switch (curChar) {
			case 59 :
				return jjStopAtPos(0, 8);
			case 73 :
			case 105 :
				return jjMoveStringLiteralDfa1_0(0x38L);
			case 79 :
			case 111 :
				return jjMoveStringLiteralDfa1_0(0x40L);
			case 80 :
			case 112 :
				return jjMoveStringLiteralDfa1_0(0x2L);
			case 84 :
			case 116 :
				return jjMoveStringLiteralDfa1_0(0x4L);
			case 126 :
				return jjStopAtPos(0, 7);
			default :
				return jjMoveNfa_0(0, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_0(long active0) {
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(0, active0);
			return 1;
		}
		switch (curChar) {
			case 76 :
			case 108 :
				return jjMoveStringLiteralDfa2_0(active0, 0x2L);
			case 78 :
			case 110 :
				if ((active0 & 0x20L) != 0L) {
					jjmatchedKind = 5;
					jjmatchedPos = 1;
				}
				return jjMoveStringLiteralDfa2_0(active0, 0x18L);
			case 82 :
			case 114 :
				return jjMoveStringLiteralDfa2_0(active0, 0x4L);
			case 85 :
			case 117 :
				return jjMoveStringLiteralDfa2_0(active0, 0x40L);
			default :
				break;
		}
		return jjStartNfa_0(0, active0);
	}

	private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L) {
			return jjStartNfa_0(0, old0);
		}
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(1, active0);
			return 2;
		}
		switch (curChar) {
			case 65 :
			case 97 :
				return jjMoveStringLiteralDfa3_0(active0, 0x6L);
			case 70 :
			case 102 :
				if ((active0 & 0x10L) != 0L) {
					return jjStartNfaWithStates_0(2, 4, 1);
				}
				break;
			case 73 :
			case 105 :
				return jjMoveStringLiteralDfa3_0(active0, 0x8L);
			case 84 :
			case 116 :
				if ((active0 & 0x40L) != 0L) {
					return jjStartNfaWithStates_0(2, 6, 1);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_0(1, active0);
	}

	private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L) {
			return jjStartNfa_0(1, old0);
		}
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(2, active0);
			return 3;
		}
		switch (curChar) {
			case 67 :
			case 99 :
				return jjMoveStringLiteralDfa4_0(active0, 0x2L);
			case 78 :
			case 110 :
				return jjMoveStringLiteralDfa4_0(active0, 0x4L);
			case 84 :
			case 116 :
				if ((active0 & 0x8L) != 0L) {
					return jjStartNfaWithStates_0(3, 3, 1);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_0(2, active0);
	}

	private final int jjMoveStringLiteralDfa4_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L) {
			return jjStartNfa_0(2, old0);
		}
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(3, active0);
			return 4;
		}
		switch (curChar) {
			case 69 :
			case 101 :
				if ((active0 & 0x2L) != 0L) {
					return jjStartNfaWithStates_0(4, 1, 1);
				}
				break;
			case 83 :
			case 115 :
				if ((active0 & 0x4L) != 0L) {
					return jjStartNfaWithStates_0(4, 2, 1);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_0(3, active0);
	}

	private final void jjCheckNAdd(int state) {
		if (jjrounds[state] != jjround) {
			jjstateSet[jjnewStateCnt++] = state;
			jjrounds[state] = jjround;
		}
	}

	private final void jjAddStates(int start, int end) {
		do {
			jjstateSet[jjnewStateCnt++] = jjnextStates[start];
		} while (start++ != end);
	}

	private final void jjCheckNAddTwoStates(int state1, int state2) {
		jjCheckNAdd(state1);
		jjCheckNAdd(state2);
	}

	private final void jjCheckNAddStates(int start, int end) {
		do {
			jjCheckNAdd(jjnextStates[start]);
		} while (start++ != end);
	}

	@SuppressWarnings("unused")
	private final void jjCheckNAddStates(int start) {
		jjCheckNAdd(jjnextStates[start]);
		jjCheckNAdd(jjnextStates[start + 1]);
	}

	static final long[] jjbitVec0 = { 0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL };

	@SuppressWarnings("unused")
	private final int jjMoveNfa_0(int startState, int curPos) {
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 20;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (;;) {
			if (++jjround == 0x7fffffff) {
				ReInitRounds();
			}
			if (curChar < 64) {
				long l = 1L << curChar;
				MatchLoop: do {
					switch (jjstateSet[--i]) {
						case 0 :
							if ((0xfffffffeffffffffL & l) != 0L) {
								if (kind > 13) {
									kind = 13;
								}
							}
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 10) {
									kind = 10;
								}
								jjCheckNAddStates(0, 2);
							} else if (curChar == 45) {
								jjstateSet[jjnewStateCnt++] = 17;
							} else if (curChar == 46) {
								jjCheckNAdd(12);
							} else if (curChar == 34) {
								jjCheckNAddTwoStates(3, 4);
							}
							break;
						case 1 :
							if ((0x3ff400000000000L & l) == 0L) {
								break;
							}
							if (kind > 9) {
								kind = 9;
							}
							jjstateSet[jjnewStateCnt++] = 1;
							break;
						case 2 :
							if (curChar == 34) {
								jjCheckNAddTwoStates(3, 4);
							}
							break;
						case 3 :
							if ((0xfffffffbfffffbffL & l) != 0L) {
								jjCheckNAddTwoStates(3, 4);
							}
							break;
						case 4 :
							if ((curChar == 34) && (kind > 9)) {
								kind = 9;
							}
							break;
						case 5 :
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 10) {
								kind = 10;
							}
							jjCheckNAddStates(0, 2);
							break;
						case 6 :
							if (curChar != 46) {
								break;
							}
							if (kind > 10) {
								kind = 10;
							}
							jjCheckNAddTwoStates(7, 8);
							break;
						case 7 :
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 10) {
								kind = 10;
							}
							jjCheckNAddTwoStates(7, 8);
							break;
						case 9 :
							if ((0x280000000000L & l) != 0L) {
								jjCheckNAdd(10);
							}
							break;
						case 10 :
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 10) {
								kind = 10;
							}
							jjCheckNAdd(10);
							break;
						case 11 :
							if (curChar == 46) {
								jjCheckNAdd(12);
							}
							break;
						case 12 :
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 10) {
								kind = 10;
							}
							jjCheckNAddTwoStates(12, 13);
							break;
						case 14 :
							if ((0x280000000000L & l) != 0L) {
								jjCheckNAdd(15);
							}
							break;
						case 15 :
							if ((0x3ff000000000000L & l) == 0L) {
								break;
							}
							if (kind > 10) {
								kind = 10;
							}
							jjCheckNAdd(15);
							break;
						case 16 :
							if (((0xfffffffeffffffffL & l) != 0L) && (kind > 13)) {
								kind = 13;
							}
							break;
						case 17 :
							if (curChar != 45) {
								break;
							}
							if (kind > 14) {
								kind = 14;
							}
							jjCheckNAdd(18);
							break;
						case 18 :
							if ((0xfffffffffffffbffL & l) == 0L) {
								break;
							}
							if (kind > 14) {
								kind = 14;
							}
							jjCheckNAdd(18);
							break;
						case 19 :
							if (curChar == 45) {
								jjstateSet[jjnewStateCnt++] = 17;
							}
							break;
						default :
							break;
					}
				} while (i != startsAt);
			} else if (curChar < 128) {
				long l = 1L << (curChar & 077);
				MatchLoop: do {
					switch (jjstateSet[--i]) {
						case 0 :
							if (kind > 13) {
								kind = 13;
							}
							if ((0x7fffffe07fffffeL & l) != 0L) {
								if (kind > 9) {
									kind = 9;
								}
								jjCheckNAdd(1);
							}
							break;
						case 1 :
							if ((0x7fffffe87ffffffL & l) == 0L) {
								break;
							}
							if (kind > 9) {
								kind = 9;
							}
							jjCheckNAdd(1);
							break;
						case 3 :
							jjAddStates(3, 4);
							break;
						case 8 :
							if ((0x2000000020L & l) != 0L) {
								jjAddStates(5, 6);
							}
							break;
						case 13 :
							if ((0x2000000020L & l) != 0L) {
								jjAddStates(7, 8);
							}
							break;
						case 16 :
							if (kind > 13) {
								kind = 13;
							}
							break;
						case 18 :
							if (kind > 14) {
								kind = 14;
							}
							jjstateSet[jjnewStateCnt++] = 18;
							break;
						default :
							break;
					}
				} while (i != startsAt);
			} else {
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do {
					switch (jjstateSet[--i]) {
						case 0 :
							if (((jjbitVec0[i2] & l2) != 0L) && (kind > 13)) {
								kind = 13;
							}
							break;
						case 3 :
							if ((jjbitVec0[i2] & l2) != 0L) {
								jjAddStates(3, 4);
							}
							break;
						case 18 :
							if ((jjbitVec0[i2] & l2) == 0L) {
								break;
							}
							if (kind > 14) {
								kind = 14;
							}
							jjstateSet[jjnewStateCnt++] = 18;
							break;
						default :
							break;
					}
				} while (i != startsAt);
			}
			if (kind != 0x7fffffff) {
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 20 - (jjnewStateCnt = startsAt))) {
				return curPos;
			}
			try {
				curChar = input_stream.readChar();
			} catch (java.io.IOException e) {
				return curPos;
			}
		}
	}

	static final int[] jjnextStates = { 5, 6, 8, 3, 4, 9, 10, 14, 15, };
	public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, "\176", "\73", null,
			null, null, null, null, null, };
	public static final String[] lexStateNames = { "DEFAULT", };
	static final long[] jjtoToken = { 0x7ffL, };
	static final long[] jjtoSkip = { 0x7000L, };
	protected SimpleCharStream input_stream;
	private final int[] jjrounds = new int[20];
	private final int[] jjstateSet = new int[40];
	protected char curChar;

	public TpnParserTokenManager(SimpleCharStream stream) {
		if (SimpleCharStream.staticFlag) {
			throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		}
		input_stream = stream;
	}

	public TpnParserTokenManager(SimpleCharStream stream, int lexState) {
		this(stream);
		SwitchTo(lexState);
	}

	public void ReInit(SimpleCharStream stream) {
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = stream;
		ReInitRounds();
	}

	private final void ReInitRounds() {
		int i;
		jjround = 0x80000001;
		for (i = 20; i-- > 0;) {
			jjrounds[i] = 0x80000000;
		}
	}

	public void ReInit(SimpleCharStream stream, int lexState) {
		ReInit(stream);
		SwitchTo(lexState);
	}

	public void SwitchTo(int lexState) {
		if ((lexState >= 1) || (lexState < 0)) {
			throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.",
					TokenMgrError.INVALID_LEXICAL_STATE);
		} else {
			curLexState = lexState;
		}
	}

	protected Token jjFillToken() {
		Token t = Token.newToken(jjmatchedKind);
		t.kind = jjmatchedKind;
		String im = jjstrLiteralImages[jjmatchedKind];
		t.image = (im == null) ? input_stream.GetImage() : im;
		t.beginLine = input_stream.getBeginLine();
		t.beginColumn = input_stream.getBeginColumn();
		t.endLine = input_stream.getEndLine();
		t.endColumn = input_stream.getEndColumn();
		return t;
	}

	int curLexState = 0;
	int defaultLexState = 0;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	@SuppressWarnings("unused")
	public Token getNextToken() {
		int kind;
		Token specialToken = null;
		Token matchedToken;
		int curPos = 0;

		EOFLoop: for (;;) {
			try {
				curChar = input_stream.BeginToken();
			} catch (java.io.IOException e) {
				jjmatchedKind = 0;
				matchedToken = jjFillToken();
				return matchedToken;
			}

			try {
				input_stream.backup(0);
				while ((curChar <= 32) && ((0x100000000L & (1L << curChar)) != 0L)) {
					curChar = input_stream.BeginToken();
				}
			} catch (java.io.IOException e1) {
				continue EOFLoop;
			}
			jjmatchedKind = 0x7fffffff;
			jjmatchedPos = 0;
			curPos = jjMoveStringLiteralDfa0_0();
			if (jjmatchedKind != 0x7fffffff) {
				if (jjmatchedPos + 1 < curPos) {
					input_stream.backup(curPos - jjmatchedPos - 1);
				}
				if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
					matchedToken = jjFillToken();
					return matchedToken;
				} else {
					continue EOFLoop;
				}
			}
			int error_line = input_stream.getEndLine();
			int error_column = input_stream.getEndColumn();
			String error_after = null;
			boolean EOFSeen = false;
			try {
				input_stream.readChar();
				input_stream.backup(1);
			} catch (java.io.IOException e1) {
				EOFSeen = true;
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
				if ((curChar == '\n') || (curChar == '\r')) {
					error_line++;
					error_column = 0;
				} else {
					error_column++;
				}
			}
			if (!EOFSeen) {
				input_stream.backup(1);
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
			}
			throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar,
					TokenMgrError.LEXICAL_ERROR);
		}
	}

}
