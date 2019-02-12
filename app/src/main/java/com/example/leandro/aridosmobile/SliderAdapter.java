package com.example.leandro.aridosmobile;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.help,
            R.drawable.reporte,
            R.drawable.resumen,
            R.drawable.reportproduccion,
            R.drawable.resumenproduc,
            R.drawable.reportsalidareport,
            R.drawable.reportsalidaresu,
            R.drawable.ajustes
    };

    public String[] slide_headings = {
            "INSTRUCCIONES",
            "Acopio - Report de Entrada",
            "Acopio - Resumen diario",
            "Producción - Report producción",
            "Producción - Resumen",
            "Report Salida - Report",
            "Report Salida - Resumen",
            "Ajustes de la Aplicación"
    };

    public String[] slide_descs = {

            "A continuación te presentaremos una pequeña ayuda para usar correctamente esta aplicación.",
            "En el menu de Acopio encontraras la opcion Report de Entrada, aqui podras dar recepción a todo el material que ingresa a la planta",
            "En resumen diario podras obtener un resumen del material acopiado en la planta por una patente determinada",
            "En el menú de Producción encontraras los Report de Producción, aqui podras generar un report sobre la producción de la planta",
            "El resumen de producción te indicara mediante un voucher una estadistica de cuanto producio la planta durante el día",
            "En el menu report salida - Report, prodrás realizar un report de la salida de material para cada patente disponible",
            "El resumen de la sección Report Salida te proporcionara un voucher diario con las patentes y la carga con la cual salieron de la planta",
            "En Ajustes podras configurar tu Aplicación con la planta en la cual te encuentres"
    };

    @Override
    public int getCount(){
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o){
        return view == (RelativeLayout) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slidepager,container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_instrucciones);
        TextView slideDescript = (TextView) view.findViewById(R.id.slide_inst);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescript.setText(slide_descs[position]);
        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position,Object object) {
        container.removeView((RelativeLayout)object);
    }
}
