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
public class King extends Piece{
    private static final int[] CANDIDATE_MOVE_COORDINATES = {-8,-1,8,1,-9,-7,7,9};
    
    public King(final int piecePosition,final Alliance pieceAlliance)
    {
        super(PieceType.KING,piecePosition,pieceAlliance,true);
    }
    
    public King(final int piecePosition,final Alliance pieceAlliance,final boolean isFirstMove)
    {
        super(PieceType.KING,piecePosition,pieceAlliance,isFirstMove);
    }
    
    @Override
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES)
        {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                if (isFirstColumnExclusion(this.piecePosition,currentCandidateOffset) || isEighthColumnExclusion(this.piecePosition,currentCandidateOffset))
                    continue;
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if(!candidateDestinationTile.isTileOccupied())
                    legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));
                else
                {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if(this.pieceAlliance != pieceAlliance)
                    {
                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                    }
                }
            }
        }
        
        return Collections.unmodifiableList(legalMoves);
    }
    
    @Override
    public String toString()
    {
        return PieceType.KING.toString();
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == 7  || 
                candidateOffset == -1 || candidateOffset == -9);
    }
    
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7  || candidateOffset == 1 || candidateOffset == 9); 
    }
    
    @Override
    public King movePiece(final Move move) {
        return new King(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance());
    }
}
