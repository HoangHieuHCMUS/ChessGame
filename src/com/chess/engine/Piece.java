/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.engine;

import java.util.Collection;
/**
 *
 * @author Phuoc
 */
public abstract class Piece 
{
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private int cachedHashCode;
    
    Piece(final PieceType pieceType,final int piecePosition,final Alliance pieceAlliance,final boolean isFirstMove)
    {
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }
    
    private int computeHashCode()
    {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof Piece))
            return false;
        final Piece otherPiece = (Piece) obj;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
               pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }
    
    @Override
    public int hashCode()
    {
        return this.cachedHashCode;
    }
    
    public PieceType getPieceType()
    {
        return this.pieceType;
    }
    
    public int getPieceValue()
    {
        return this.pieceType.getPieceValue();
    }
    
    public Alliance getPieceAlliance()
    {
        return this.pieceAlliance;
    }
    
    public int getPiecePosition()
    {
        return this.piecePosition;
    }
    
    public boolean isFirstMove()
    {
        return this.isFirstMove;
    }
    
    public abstract Collection<Move> calculateLegalMoves(final Board board);  
    
    public abstract Piece movePiece(Move move);
    
    public enum PieceType
    {
        PAWN("P",100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R",500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q",900) {
            @Override
            public boolean isKing() {
                return false;
            }
            
            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K",10000) {
            @Override
            public boolean isKing() {
                return true;
            }
            
            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B",300) {
            @Override
            public boolean isKing() {
                return false;
            }
            
            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N",300) {
            @Override
            public boolean isKing() {
                return false;
            }
            
            @Override
            public boolean isRook() {
                return false;
            }
        };
        
        private String pieceName;
        private int pieceValue;
        
        PieceType(final String pieceName, final int pieceValue)
        {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }
        
        @Override
        public String toString()
        {
            return this.pieceName;
        }
        
        public int getPieceValue()
        {
            return this.pieceValue;
        }
        
        public abstract boolean isKing();
        public abstract boolean isRook();
    }
}
