package com.xtkj.testnewsframe.page.font;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.model.FontHtmlContent;
import com.xtkj.testnewsframe.page.DetailWebActivity;
import com.xtkj.testnewsframe.page.base.LActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class FontDisplayActivity extends LActivity {

    @BindView(R.id.tv_text2)
    TextView tv_text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_display);
        ButterKnife.bind(this);
        setTitle("测试自定义Title字体");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnCheckedChanged(R.id.btn_lanting)
    public void onLantingChanged(Switch view) {
        if (view.isChecked()) {
            tv_text2.setTypeface(TypefaceUtils.load(getAssets(),"HKGIRLW5.TTF"));
        }else {
            tv_text2.setTypeface(Typeface.DEFAULT);
        }
    }

    @OnClick(R.id.btn_web)
    public void onBtnWeb(View view) {
        String html="</P><P style=\"TEXT-INDENT: 2em\">腾讯体育8月4日讯 据英国《<!--keyword--><a class=\"a-tips-Article-QQ\" href=\"http://nbadata.sports.qq.com/team/21/teaminfo.html\" target=\"_blank\"><!--/keyword-->太阳<!--keyword--></a><!--/keyword-->报》消息，曼联“终于”完成了博格巴的交易，为了引进法国球星，曼联付出了1.2亿英镑，其中包括9500万英镑的转会费和2500万英镑的经纪人佣金，而在接受采访时，穆里尼奥也对此做出了暗示。</P><P style=\"TEXT-INDENT: 2em\">博格巴的交易犹如一出肥皂剧，正当外界认为曼联肯定会完成交易时，昨天博格巴接受了一位球迷的采访，法国人透露会继续留在尤文<!--keyword-->(<a class=\"a-tips-Article-QQ\" href=\"http://juventus.qq.com/\" target=\"_blank\">官网</a> <a class=\"a-tips-Article-QQ\" href=\"http://soccerdata.sports.qq.com/team/128.htm\" target=\"_blank\">数据</a>) <!--/keyword-->图斯，这让不少媒体认为曼联可能在转会市场遭遇重创，不过博格巴的话似乎也有玩笑的可能。</P><P style=\"TEXT-INDENT: 2em\">今天的英国媒体继续聚焦博格巴的转会，《太阳报》指出，曼联终于完成了这笔交易，付出的费用是1.2亿英镑，包括支付给尤文图斯的9500万英镑转会费和支付给拉伊奥拉的2500万英镑经纪人佣金，英国记者Duncan Castles联系了穆里尼奥的经纪人门德斯，后者告诉他，博格巴的交易已经完成。虽然门德斯并非博格巴的经纪人，但是他却是穆帅的经纪人，因此，可以做出理解的是，穆帅可能已经将消息透露给了门德斯。</P><P style=\"TEXT-INDENT: 2em\">穆里尼奥在接受采访时也谈到了博格巴，《每日邮报》指出，穆帅暗示曼联已经完成了交易，“我不想谈论<!--keyword--><a class=\"a-tips-Article-QQ\" href=\"http://nbadata.sports.qq.com/player/229598/playerinfo.html\" target=\"_blank\"><!--/keyword-->保罗<!--keyword--></a><!--/keyword-->，因为他是一名尤文图斯球员，但是现实是，我们还会签下一名球员，据你所知，转会市场会在8月31日关闭，我们还有足够的时间，但是显然，我希望在新赛季开始前完成。”</P><P style=\"TEXT-INDENT: 2em\">如果完成博格巴的交易，那么曼联就会在今夏签下四名球员，此前三人分别是拜利、<a class=\"a-tips-Article-QQ\" href=\"http://sports.qq.com/d/f_players/7/6042\" target=\"_blank\">姆希塔良</a>和<!--keyword--><a class=\"a-tips-Article-QQ\" href=\"http://sports.qq.com/d/f_players/3/2628\" target=\"_blank\"><!--/keyword-->伊布<!--keyword--></a><!--/keyword-->。</P><P style=\"TEXT-INDENT: 2em\">（烟圈）</P>";
        FontHtmlContent c=new FontHtmlContent(html);
        Intent t=new Intent(this, DetailWebActivity.class);
        t.putExtra("webContentObj",c);
        startActivity(t);
    }
}
