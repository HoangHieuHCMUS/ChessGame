/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.engine;

import com.chess.engine.Move.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Phuoc
 */
public class Pawn extends Piece {
    private static final int[] CANDIDATE_MOVE_COORDINATES = {8,16,7,9};
   
    public Pawn(final int piecePosition,final Alliance pieceAlliance)
    {
        super(PieceType.PAWN,piecePosition,pieceAlliance,true);
    }
    
    public Pawn(final int piecePosition,final Alliance pieceAlliance,final boolean isFirstMove)
    {
        super(PieceType.PAWN,piecePosition,pieceAlliance,isFirstMove);
    }
    
    @Override 
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES)
        {
            final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidateOffset);
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
                continue;
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied())
            {
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate))   
                    legalMoves.add(new PawnPromotion(new PawnMove(board,this,candidateDestinationCoordinate)));
                else
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
            }
            else if (currentCandidateOffset == 16 && this.isFirstMove() && 
                    ((BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) || 
                    (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isWhite())))
            {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() && 
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied())
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
            }
            else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))))
            {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied())
                {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                    {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate))   
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate)));
                        else
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
                else if (board.getEnPassantPawn() != null)
                {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection())))
                    {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                            legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
            }
            else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack() ||
                    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()))))
            {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied())
                {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                    {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate))   
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate)));
                        else
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
                else if (board.getEnPassantPawn() != null)
                 {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection())))
                    {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                            legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
            }        
        }
        return Collections.unmodifiableList(legalMoves);
    }
    
    @Override
    public String toString()
    {
        return PieceType.PAWN.toString();
    }
    
    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance());
    }
    
    public Piece getPromotionPiece()
    {
        return new Queen(this.piecePosition,this.pieceAlliance,false);
    }
}
