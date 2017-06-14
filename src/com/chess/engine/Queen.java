/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.engine;

import com.chess.engine.Move.MajorAttackMove;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Phuoc
 */
public class Queen extends Piece {
    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-8,-1,8,1,-9,-7,7,9};
    
    public Queen(final int piecePosition,final Alliance pieceAlliance)
    {
        super(PieceType.QUEEN,piecePosition,pieceAlliance,true);
    }
    
    public Queen(final int piecePosition,final Alliance pieceAlliance,final boolean isFirstMove)
    {
        super(PieceType.QUEEN,piecePosition,pieceAlliance,isFirstMove);
    }
    
    @Override
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES)
        {
            int candidateDestinationCoordinate = this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                if (isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset) || isEighthColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset))
                    break;       
                candidateDestinationCoordinate += candidateCoordinateOffset; 
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
                {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if(!candidateDestinationTile.isTileOccupied())
                        legalMoves.add(new Move.MajorMove(board,this,candidateDestinationCoordinate));
                    else
                    {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if(this.pieceAlliance != pieceAlliance)
                        {
                            legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                        }
                        break;
                    }   
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
    
    @Override
    public String toString()
    {
        return PieceType.QUEEN.toString();
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9  || candidateOffset == 7 || candidateOffset == -1);
    }
    
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 7  || candidateOffset == -7 || candidateOffset == 1);
    }
    
    @Override
    public Queen movePiece(final Move move) {
        return new Queen(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance());
    }
}