/*
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.createMock;

import java.util.List;

import org.jcvi.TestUtil;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.num.DefaultShortGlyphCodec;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.junit.Test;

public class TestPositionFastaRecord {

    private String id = "identifier";
    private String comment = "comment";
    
    private short[] positions = new short[]{1, 10,21,31,42,50,62,84,90,101,110,121,130,140,152};
    private static final GlyphCodec<ShortGlyph> CODEC = DefaultShortGlyphCodec.getInstance();
    
    EncodedGlyphs<ShortGlyph> encodedPositions = 
        new DefaultEncodedGlyphs<ShortGlyph>(CODEC,
                ShortGlyphFactory.getInstance().getGlyphsFor(positions));
    
    DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> sut = new DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>>(id,comment, encodedPositions);
    
    private String buildExpectedRecord(DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> fasta){
        StringBuilder builder= new StringBuilder();
        builder.append(">")
                    .append(fasta.getIdentifier());
        if(fasta.getComments() !=null){
            builder.append(" ")
                    .append(fasta.getComments());
        }
        appendCarriageReturn(builder);
        List<ShortGlyph> pos = fasta.getValues().decode();
        for(int i=1; i<pos.size(); i++){
            
            builder.append(String.format("%04d", pos.get(i-1).getNumber()));
            if(i%12==0){
                appendCarriageReturn(builder);
            }
            else{
                builder.append(" ");
            }
        }
        builder.append(String.format("%04d", pos.get(pos.size() -1).getNumber()));
        appendCarriageReturn(builder);
        return builder.toString();
    }
    
    private void appendCarriageReturn(StringBuilder builder) {
        builder.append('\n');
    }
    
    @Test
    public void constructor(){
        assertEquals(comment, sut.getComments());
        assertConstructedFieldsCorrect(sut);
    }

    private void assertConstructedFieldsCorrect(DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> fasta) {
        assertEquals(id, fasta.getIdentifier());        
        assertEquals(0L, fasta.getChecksum());
        final String expectedRecord = buildExpectedRecord(fasta);
        assertEquals(expectedRecord, fasta.getStringRecord().toString());
        assertEquals(encodedPositions, fasta.getValues());
    }
    @Test
    public void constructorWithoutComment(){
        DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> noComment = new DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>>(id,encodedPositions);
        
        assertNull(noComment.getComments());
        assertConstructedFieldsCorrect(noComment);
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameId(){
        DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> sameIdAndComment = new DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>>(
                id,comment,createMock(EncodedGlyphs.class));
        TestUtil.assertEqualAndHashcodeSame(sut, sameIdAndComment);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsNotAQualityFasta(){
        assertFalse(sut.equals(createMock(DefaultEncodedNucleotideFastaRecord.class)));
    }
    @Test
    public void equalsDifferentComment(){
        DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> differentComment = new DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>>(
                id,null,createMock(EncodedGlyphs.class));
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    @Test
    public void notEqualsDifferentId(){
        DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> differentId = new DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>>(
                "different"+id,comment,createMock(EncodedGlyphs.class));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
}
